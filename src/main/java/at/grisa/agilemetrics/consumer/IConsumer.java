package at.grisa.agilemetrics.consumer;

import at.grisa.agilemetrics.entity.Metric;

public interface IConsumer {
    void consume(Metric measurement);
}
