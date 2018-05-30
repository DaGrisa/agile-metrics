package at.grisa.agilemetrics.producer.sonarqube.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeProducer;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeRestClient;
import at.grisa.agilemetrics.producer.sonarqube.restentity.Component;
import at.grisa.agilemetrics.producer.sonarqube.restentity.Measure;
import at.grisa.agilemetrics.producer.sonarqube.restentity.Metric;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SonarQubeProducer.class, SonarQubeProducerMockConfiguration.class, ApplicationConfig.class, CredentialManager.class, PropertyManager.class})
@TestPropertySource("classpath:sonarqube-test.properties")
public class SonarQubeProducerMetricsTest {
    @Autowired
    private SonarQubeRestClient sonarQubeRestClient;
    @Autowired
    private SonarQubeProducer sonarQubeProducer;
    @Autowired
    private MetricQueue metricQueue;

    private final String componentKey = "component.key";
    private final String componentName = "Component Name";
    private Measure measureCoverage;
    private Measure measureTestExecutionTime;

    @Before
    public void createMocks() {
        Component component = new Component();
        component.setKey(componentKey);
        component.setName(componentName);
        when(sonarQubeRestClient.getComponents()).thenReturn(Arrays.asList(component));

        measureCoverage = new Measure();
        measureCoverage.setMetric("Coverage");
        measureCoverage.setValue(78.48);
        measureTestExecutionTime = new Measure();
        measureTestExecutionTime.setMetric("Test Execution Time");
        measureTestExecutionTime.setValue(256.34);
        when(sonarQubeRestClient.getMeasures(componentKey, Metric.COVERAGE, Metric.TEST_EXECUTION_TIME)).thenReturn(Arrays.asList(measureCoverage, measureTestExecutionTime));
    }

    @Test
    public void metricsTest() {
        sonarQubeProducer.produce();

        Map<String, String> metaCoverage = new HashMap<>();
        metaCoverage.put("component", componentName);
        at.grisa.agilemetrics.entity.Metric coverage = new at.grisa.agilemetrics.entity.Metric(measureCoverage.getValue(), "SonarQube - " + measureCoverage.getMetric(), metaCoverage);

        Map<String, String> metaTestExecutionTime = new HashMap<>();
        metaTestExecutionTime.put("component", componentName);
        at.grisa.agilemetrics.entity.Metric testExecutionTime = new at.grisa.agilemetrics.entity.Metric(measureTestExecutionTime.getValue(), "SonarQube - " + measureTestExecutionTime.getMetric(), metaTestExecutionTime);

        verify(metricQueue).enqueueMetric(coverage);
        verify(metricQueue).enqueueMetric(testExecutionTime);
    }
}
