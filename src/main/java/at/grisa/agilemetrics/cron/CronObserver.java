package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.entity.Measurement;
import at.grisa.agilemetrics.producer.IProducer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

public class CronObserver {
    private LinkedList<IConsumer> consumers;
    private LinkedList<IProducer> producers;

    @Autowired
    MeasurementQueue measurementQueue;

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

    public void activateConsumer() {
        Measurement measurement = measurementQueue.dequeueMeasurement();
        for (IConsumer consumer : consumers) {
            consumer.consume(measurement);
        }
    }

    public void activateProducer() {
        for (IProducer producer : producers) {
            producer.produce(measurementQueue);
        }
    }
}
