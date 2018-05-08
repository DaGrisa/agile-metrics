package at.grisa.agilemetrics.consumer.elasticsearch;

import at.grisa.agilemetrics.consumer.elasticsearch.restentity.BulkResponse;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Collection;

public class ElasticSearchRestClient {
    private final static org.apache.logging.log4j.Logger log = LogManager.getLogger(SonarQubeProducer.class.getName());

    private final String hostUrl;
    private final String INDEXNAME = "agilemetrics";
    private final String TYPENAME = "metric";

    public ElasticSearchRestClient(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public void saveMetrics(Collection<Metric> metrics) {
        String bulkUrl = this.hostUrl + "/_bulk";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
        String bulkIndexLine = "{ \"index\" : { \"_index\" : \"" + INDEXNAME + "\", \"_type\" : \"" + TYPENAME + "\"} }\n";
        StringBuilder requestBuilder = new StringBuilder();
        for (Metric metric : metrics) {
            try {
                String jsonValue = objectMapper.writeValueAsString(metric);
                requestBuilder.append(bulkIndexLine);
                requestBuilder.append(jsonValue + "\n");
            } catch (JsonProcessingException e) {
                log.error("Error when converting to JSON", e);
            }
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBuilder.toString());

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(bulkUrl);
        RestTemplate restTemplate = new RestTemplate();
        BulkResponse response = restTemplate.exchange(uriComponentsBuilder.build().encode().toUri(),
                HttpMethod.POST,
                httpEntity,
                BulkResponse.class
        ).getBody();

        if (response.getErrors()) {
            log.error("Error sending bulk metrics to elasticsearch.");
        }
    }
}
