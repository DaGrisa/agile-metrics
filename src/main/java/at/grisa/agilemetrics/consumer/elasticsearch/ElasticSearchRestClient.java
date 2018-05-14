package at.grisa.agilemetrics.consumer.elasticsearch;

import at.grisa.agilemetrics.consumer.elasticsearch.restentity.BulkResponse;
import at.grisa.agilemetrics.cron.MetricErrorHandler;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeProducer;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Collection;

@Component
@Lazy
public class ElasticSearchRestClient {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(SonarQubeProducer.class.getName());

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

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBuilder.toString());

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(bulkUrl);
        RestTemplate restTemplate = new RestTemplate();

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

        if (response != null && response.getErrors()) {
            log.error("Error sending bulk metrics to elasticsearch.");
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
