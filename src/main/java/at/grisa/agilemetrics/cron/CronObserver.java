package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.IProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class CronObserver {
    private final static Logger log = LogManager.getLogger(CronObserver.class);
    private LinkedList<IConsumer> consumers;
    private LinkedList<IProducer> producers;

    @Autowired
    MetricQueue metricQueue;

    public CronObserver() {
        consumers = new LinkedList<>();
        producers = new LinkedList<>();
    }

    public void registerConsumer(IConsumer consumer) {
        if (!consumers.contains(consumer)) {
            consumers.add(consumer);
        }
    }

    public void registerProducer(IProducer producer) {
        if (!producers.contains(producer)) {
            producers.add(producer);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void activateConsumer() {
        log.debug("Checking metrics queue...");
        Metric metric = metricQueue.dequeueMetric();
        while (metric != null) {
            log.debug("Metric found, sending it to all consumers and then enqueue it.", metric);
            for (IConsumer consumer : consumers) {
                consumer.consume(metric);
            }
            metric = metricQueue.dequeueMetric();
        }
    }

    @Scheduled(cron = "${cron.expression.daily:0 10 0 * * ?}")
    public void activateProducerDaily() {
        for (IProducer producer : producers) {
            producer.produce(TimeSpan.DAILY);
        }
    }
}
