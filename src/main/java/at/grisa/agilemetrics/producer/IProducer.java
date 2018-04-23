package at.grisa.agilemetrics.producer;

import at.grisa.agilemetrics.cron.MeasurementQueue;
import at.grisa.agilemetrics.cron.TimeSpan;

public interface IProducer {
    void produce(MeasurementQueue measurementQueue, TimeSpan timespan);
}
