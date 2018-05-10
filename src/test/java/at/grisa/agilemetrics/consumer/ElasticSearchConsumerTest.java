package at.grisa.agilemetrics.consumer;

import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchConsumer;
import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchRestClient;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ElasticSearchConsumer.class, CredentialManager.class, PropertyManager.class, ElasticSearchRestClientMockConfiguration.class})
@TestPropertySource("classpath:elasticsearch-test.properties")
public class ElasticSearchConsumerTest {
    @Autowired
    ElasticSearchRestClient restClient;
    @Autowired
    ElasticSearchConsumer consumer;

    @Test
    public void consumesMetrics() {
        HashMap<String, String> meta = new HashMap<>();
        meta.put("meta", "data");

        HashSet<String> tags = new HashSet<>();
        tags.add("some");
        tags.add("tags");

        Metric metric1 = new Metric(1.0, "metric 1", meta, tags);
        Metric metric2 = new Metric(19557439.23859, "metric 2", meta, tags);
        Metric metric3 = new Metric(495.133, "metric 3", meta, tags);

        consumer.consume(metric1);
        consumer.consume(metric2);
        consumer.consume(metric3);

        ArgumentCaptor<Collection> argumentCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(restClient).saveMetrics(argumentCaptor.capture());
        assertEquals("called method on rest client to save metrics", Arrays.asList(metric1, metric2, metric3), argumentCaptor.getValue());
    }
}
