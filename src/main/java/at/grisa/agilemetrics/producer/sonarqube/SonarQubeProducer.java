package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.sonarqube.restentity.Component;
import at.grisa.agilemetrics.producer.sonarqube.restentity.Measure;
import at.grisa.agilemetrics.producer.sonarqube.restentity.Metric;
import at.grisa.agilemetrics.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@org.springframework.stereotype.Component
public class SonarQubeProducer implements IProducer {
    private static final Logger log = LogManager.getLogger(SonarQubeProducer.class);

    @Autowired
    private SonarQubeRestClient sonarQubeRestClient;

    @Autowired
    private MetricQueue metricQueue;

    @Autowired
    private PropertyManager propertyManager;

    @Override
    public boolean checkConnection() {
        try {
            return sonarQubeRestClient.checkConnection();
        } catch (Exception e) {
            log.error("could not connect to SonarQube, check error message", e);
            return false;
        }
    }

    @Override
    public void produce() {
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

            // failsafe execution
            try {
                for (Component component : sonarQubeRestClient.getComponents()) {
                    Collection<Measure> measures = sonarQubeRestClient.getMeasures(component.getKey(), metrics);
                    for (Measure measure : measures) {
                        Map<String, String> meta = new HashMap<>();
                        meta.put("component", component.getName());
                        metricQueue.enqueueMetric(new at.grisa.agilemetrics.entity.Metric(measure.getValue(), "SonarQube - " + measure.getMetric(), meta));
                    }

                }
            } catch (Exception e) {
                log.error("Error producing metric.", e);
            }

        }
    }
}
