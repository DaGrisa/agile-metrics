package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.entity.Measurement;
import at.grisa.agilemetrics.producer.IProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class CronObserver {
    private LinkedList<IConsumer> consumers;
    private LinkedList<IProducer> producersDaily;

    @Autowired
    MeasurementQueue measurementQueue;

    public CronObserver() {
        consumers = new LinkedList<>();
        producersDaily = new LinkedList<>();
    }

    public void registerConsumer(IConsumer consumer) {
        if (!consumers.contains(consumer)) {
            consumers.add(consumer);
        }
    }

    public void registerProducerDaily(IProducer producer) {
        if (!producersDaily.contains(producer)) {
            producersDaily.add(producer);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void activateConsumer() {
        Measurement measurement = measurementQueue.dequeueMeasurement();
        while (measurement != null) {
            for (IConsumer consumer : consumers) {
                consumer.consume(measurement);
            }
            measurement = measurementQueue.dequeueMeasurement();
        }
    }

    @Scheduled(cron = "${cron.expression.daily}")
    public void activateProducerDaily() {
        for (IProducer producer : producersDaily) {
            producer.produce(measurementQueue);
        }
    }
}
