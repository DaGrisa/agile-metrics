package at.grisa.agilemetrics.producer;

public interface IProducer {
    void produce();

    boolean checkConnection();
}
