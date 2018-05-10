package at.grisa.agilemetrics.consumer.elasticsearch;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.util.PropertyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
@Lazy
public class ElasticSearchConsumer implements IConsumer {
    private ElasticSearchRestClient restClient;
    private Collection<Metric> metricsQueue;
    private PropertyManager propertyManager;

    @Autowired
    public ElasticSearchConsumer(ElasticSearchRestClient restClient, PropertyManager propertyManager) {
        this.restClient = restClient;
        this.metricsQueue = new ArrayList<>();
        this.propertyManager = propertyManager;
    }

    @Override
    public void consume(Metric metric) {
        collectForBatch(metric);
    }

    private void collectForBatch(Metric metric) {
        this.metricsQueue.add(metric);
        if (metricsQueue.size() >= propertyManager.getElasticSearchBatchSize()) {
            checkAndSave();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void checkAndSave() {
        if (!metricsQueue.isEmpty()) {
            restClient.saveMetrics(new ArrayList<>(metricsQueue));
            metricsQueue.clear();
        }
    }
}
