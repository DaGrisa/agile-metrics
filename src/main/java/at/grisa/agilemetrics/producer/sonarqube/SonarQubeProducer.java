package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.cron.TimeSpan;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Component;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Measure;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Metric;
import at.grisa.agilemetrics.util.CredentialManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SonarQubeProducer implements IProducer {
    @Override
    public void produce(MetricQueue metricQueue, TimeSpan timespan) {
        CredentialManager credentialManager = new CredentialManager();
        SonarQubeRestClient sonarQubeRestClient = new SonarQubeRestClient(credentialManager.getSonarqubeBaseUrl(), credentialManager.getSonarqubeUsername(), credentialManager.getSonarqubePassword());

        produceCoverage(metricQueue, sonarQubeRestClient);
    }

    public void produceCoverage(MetricQueue metricQueue, SonarQubeRestClient sonarQubeRestClient) {
        for (Component component : sonarQubeRestClient.getComponents()) {
            Collection<Measure> measures = sonarQubeRestClient.getMeasures(component.getKey(), Metric.COVERAGE);
            for (Measure measure : measures) {
                Map<String, String> meta = new HashMap<>();
                meta.put("component", component.getName());
                metricQueue.enqueueMetric(new at.grisa.agilemetrics.entity.Metric(measure.getValue(), "SonarQube - " + measure.getMetric(), meta));
            }
        }
    }
}
