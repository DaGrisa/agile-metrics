package at.grisa.agilemetrics.api;

import java.time.ZonedDateTime;
import java.util.List;

public class Information {
    private List<String> consumers;
    private List<String> producers;
    private Integer processedMetricsLastRun;
    private ZonedDateTime lastRun;

    public Information() {
    }

    public Information(List<String> consumers, List<String> producers, Integer processedMetrics, ZonedDateTime lastRun) {
        this.consumers = consumers;
        this.producers = producers;
        this.processedMetricsLastRun = processedMetrics;
        this.lastRun = lastRun;
    }

    public List<String> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<String> consumers) {
        this.consumers = consumers;
    }

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers(List<String> producers) {
        this.producers = producers;
    }

    public Integer getProcessedMetricsLastRun() {
        return processedMetricsLastRun;
    }

    public void setProcessedMetricsLastRun(Integer processedMetricsLastRun) {
        this.processedMetricsLastRun = processedMetricsLastRun;
    }

    public ZonedDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(ZonedDateTime lastRun) {
        this.lastRun = lastRun;
    }
}
