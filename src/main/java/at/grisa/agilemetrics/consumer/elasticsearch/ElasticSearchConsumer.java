package at.grisa.agilemetrics.consumer.elasticsearch;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
@Lazy
public class ElasticSearchConsumer implements IConsumer {
    private static final Logger log = LogManager.getLogger(ElasticSearchConsumer.class);

    private ElasticSearchRestClient restClient;
    private Collection<Metric> metrics;
    private PropertyManager propertyManager;

    @Autowired
    public ElasticSearchConsumer(ElasticSearchRestClient restClient, PropertyManager propertyManager) {
        this.restClient = restClient;
        this.metrics = new ArrayList<>();
        this.propertyManager = propertyManager;
    }

    @Override
    public boolean checkConnection() {
        try {
            return restClient.checkConnection();
        } catch (Exception e) {
            log.error("could not connect to ElasticSearch, check error message", e);
            return false;
        }
    }

    @Override
    public void consume(Metric metric) {
        collectForBatch(metric);
    }

    private void collectForBatch(Metric metric) {
        this.metrics.add(metric);
        if (metrics.size() >= propertyManager.getElasticSearchBatchSize()) {
            checkAndSave();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void checkAndSave() {
        if (!metrics.isEmpty()) {
            restClient.saveMetrics(new ArrayList<>(metrics));
            metrics.clear();
        }
    }
}
