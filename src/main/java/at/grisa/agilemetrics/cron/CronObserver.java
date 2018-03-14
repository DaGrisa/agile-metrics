package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.consumer.IConsumer;
import at.grisa.agilemetrics.producer.IProducer;

import java.util.LinkedList;

public class CronObserver {
    private LinkedList<IConsumer> consumers;
    private LinkedList<IProducer> producers;

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

    public void notifyConsumer(String message) {
        for (IConsumer consumer : consumers) {
            consumer.notification(message);
        }
    }

    public void notifyProducer(String message) {
        for (IProducer producer : producers) {
            producer.notification(message);
        }
    }
}
