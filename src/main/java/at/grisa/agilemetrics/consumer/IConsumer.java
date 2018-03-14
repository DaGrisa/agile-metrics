package at.grisa.agilemetrics.consumer;

import at.grisa.agilemetrics.entity.Measurement;

public interface IConsumer {
    void consume(Measurement measurement);
}
