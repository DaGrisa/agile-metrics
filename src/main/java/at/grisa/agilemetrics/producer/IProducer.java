package at.grisa.agilemetrics.producer;

import at.grisa.agilemetrics.cron.MeasurementQueue;

public interface IProducer {
    void produce(MeasurementQueue measurementQueue);
}
