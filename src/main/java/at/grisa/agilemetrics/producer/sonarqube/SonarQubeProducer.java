package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.cron.TimeSpan;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Component;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Measure;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Metric;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SonarQubeProducer implements IProducer {
    private final static Logger log = LogManager.getLogger(SonarQubeProducer.class.getName());

    @Override
    public void produce(MetricQueue metricQueue, TimeSpan timespan) {
        CredentialManager credentialManager = new CredentialManager();
        SonarQubeRestClient sonarQubeRestClient = new SonarQubeRestClient(credentialManager.getSonarqubeBaseUrl(), credentialManager.getSonarqubeUsername(), credentialManager.getSonarqubePassword());

        PropertyManager propertyManager = new PropertyManager();
        Collection<Metric> metricsList = new LinkedList<>();

        if (propertyManager.getSonarqubeMetrics() != null) {
            for (String metric : propertyManager.getSonarqubeMetrics().split(",")) {
                try { // only take known and checked values
                    Metric newMetric = Metric.valueOf(metric);
                    metricsList.add(newMetric);
                } catch (IllegalArgumentException e) {
                    log.error(e);
                }
            }

            Metric[] metrics = new Metric[0];
            metrics = metricsList.toArray(metrics);

            for (Component component : sonarQubeRestClient.getComponents()) {
                Collection<Measure> measures = sonarQubeRestClient.getMeasures(component.getKey(), metrics);
                for (Measure measure : measures) {
                    Map<String, String> meta = new HashMap<>();
                    meta.put("component", component.getName());
                    metricQueue.enqueueMetric(new at.grisa.agilemetrics.entity.Metric(measure.getValue(), "SonarQube - " + measure.getMetric(), meta));
                }

            }
        }
    }
}
