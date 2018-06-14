package at.grisa.agilemetrics.consumer.elasticsearch;

import at.grisa.agilemetrics.consumer.elasticsearch.restentity.BulkResponse;
import at.grisa.agilemetrics.consumer.elasticsearch.restentity.ClusterInfo;
import at.grisa.agilemetrics.cron.MetricErrorHandler;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collection;

@Component
@Lazy
public class ElasticSearchRestClient {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(ElasticSearchRestClient.class);
    private HttpComponentsClientHttpRequestFactory httpRequestFactory;

    @Autowired
    private MetricErrorHandler metricErrorHandler;

    private final String hostUrl;
    private final String indexName;
    private final String typeName;
    private static final String INDEXNAME_DEFAULT = "agilemetrics";
    private static final String TYPENAME_DEFAULT = "metric";

    @Autowired
    public ElasticSearchRestClient(CredentialManager credentialManager, PropertyManager propertyManager) {
        this.hostUrl = credentialManager.getElasicsearchBaseUrl();
        this.indexName = propertyManager.getElasticSearchIndexName();
        this.typeName = propertyManager.getElasticSearchTypeName();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        HttpClient httpClient;

        if (credentialManager.isProxyAuthActive()) {
            httpClient = getHttpClientProxyAuth(clientBuilder, credentialManager.getHttpProxyHost(), credentialManager.getHttpProxyPort(), credentialManager.getHttpProxyUser(), credentialManager.getHttpProxyPassword());
        } else if (credentialManager.isProxyActive()) {
            httpClient = getHttpClientProxy(clientBuilder, credentialManager.getHttpProxyHost(), credentialManager.getHttpProxyPort());
        } else {
            httpClient = clientBuilder.build();
        }

        httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient);
        httpRequestFactory.setConnectionRequestTimeout(30000);
        httpRequestFactory.setConnectTimeout(30000);
        httpRequestFactory.setReadTimeout(30000);
    }

    private HttpClient getHttpClientProxyAuth(HttpClientBuilder clientBuilder, String host, Integer port, String username, String password) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));
        HttpHost myProxy = new HttpHost(host, port);
        clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider);
        return clientBuilder.build();
    }

    private HttpClient getHttpClientProxy(HttpClientBuilder clientBuilder, String host, Integer port) {
        HttpHost myProxy = new HttpHost(host, port);
        clientBuilder.setProxy(myProxy);
        return clientBuilder.build();
    }

    public boolean checkConnection() {
        String restUrl = hostUrl;
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl);

        ClusterInfo clusterInfo = restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                null,
                ClusterInfo.class
        ).getBody();

        return clusterInfo != null && !clusterInfo.isEmpty();
    }

    public void saveMetrics(Collection<Metric> metrics) {
        String bulkUrl = this.hostUrl + "/_bulk";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
        String bulkIndexLine = "{ \"index\" : { \"_index\" : \"" + getIndexName() + "\", \"_type\" : \"" + getTypeName() + "\"} }\n";
        StringBuilder requestBuilder = new StringBuilder();
        for (Metric metric : metrics) {
            try {
                String jsonValue = objectMapper.writeValueAsString(metric);
                requestBuilder.append(bulkIndexLine);
                requestBuilder.append(jsonValue + "\n");
            } catch (JsonProcessingException e) {
                log.error("Error when converting to JSON", e);
                metricErrorHandler.saveErrorMetric(metric);
            }
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBuilder.toString(), headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(bulkUrl);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        BulkResponse response = null;
        try {
            response = restTemplate.exchange(uriComponentsBuilder.build().encode().toUri(),
                    HttpMethod.POST,
                    httpEntity,
                    BulkResponse.class
            ).getBody();
        } catch (ResourceAccessException e) {
            log.error("ElasticSearch host not reachable!", e);
            metricErrorHandler.saveErrorMetrics(metrics);
        }

        if (response != null && response.getErrors() != null && response.getErrors()) {
            log.error("Error sending bulk metrics to elasticsearch.");
            log.debug("Response: " + response);
            metricErrorHandler.saveErrorMetrics(metrics);
        }
    }

    public String getIndexName() {
        return indexName == null ? INDEXNAME_DEFAULT : indexName;
    }

    public String getTypeName() {
        return typeName == null ? TYPENAME_DEFAULT : typeName;
    }
}
