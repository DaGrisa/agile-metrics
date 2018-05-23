package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.persistence.IStatisticRepository;
import at.grisa.agilemetrics.persistence.entity.Statistic;
import at.grisa.agilemetrics.producer.IProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CronObserver {
    private static final Logger log = LogManager.getLogger(CronObserver.class);
    private LinkedList<IConsumer> consumers;
    private LinkedList<IProducer> producers;

    @Autowired
    private MetricQueue metricQueue;

    @Autowired
    private IStatisticRepository statisticRepository;

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
        if (!consumers.isEmpty()) {
            log.debug("Checking metrics queue...");
            Metric metric = metricQueue.dequeueMetric();
            while (metric != null) {
                log.debug("Metric found, sending it to all consumers and then enqueue it.", metric);
                for (IConsumer consumer : consumers) {
                    // failsafe consuming
                    try {
                        consumer.consume(metric);
                        log.debug("Metric send to consumer " + consumer, metric);
                    } catch (Exception e) {
                        log.error("Error sending metric (" + metric + ") to consumer (" + consumer + ").", e);
                    }
                }
                metric = metricQueue.dequeueMetric();
            }
        } else {
            log.error("no consumers registered");
        }
    }

    @Scheduled(cron = "${cron.expression.daily:0 10 0 * * ?}")
    public void activateProducerDaily() {
        metricQueue.resetMetricsCounter();
        producers.parallelStream().forEach(producer -> safeProduce(producer));
        statisticRepository.save(new Statistic(metricQueue.getMetricsCounter(), ZonedDateTime.now()));
    }

    /**
     * Failsafe producing
     *
     * @param producer
     */
    private void safeProduce(final IProducer producer) {
        try {
            producer.produce();
        } catch (Exception e) {
            log.error("Error producing metrics in producer " + producer, e);
        }
    }

    public List<String> getConsumersAsString() {
        return consumers.stream().map(IConsumer::toString).collect(Collectors.toList());
    }

    public List<String> getProducersAsString() {
        return producers.stream().map(IProducer::toString).collect(Collectors.toList());
    }
}
