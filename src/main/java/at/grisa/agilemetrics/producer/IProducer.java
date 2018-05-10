package at.grisa.agilemetrics.producer;

import at.grisa.agilemetrics.cron.TimeSpan;

public interface IProducer {
    void produce(TimeSpan timespan);
}
