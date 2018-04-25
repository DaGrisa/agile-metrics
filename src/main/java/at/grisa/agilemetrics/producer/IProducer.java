package at.grisa.agilemetrics.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.cron.TimeSpan;

public interface IProducer {
    void produce(MetricQueue metricQueue, TimeSpan timespan);
}
