package at.grisa.agilemetrics.producer.sonarqube.restentity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetricTest {
    @Test(expected = IllegalArgumentException.class)
    public void unknownMetric() {
        Metric.getMetric("unknown metric");
    }

    @Test
    public void knownMetrics() {
        Metric coverage = Metric.getMetric("coverage");
        assertEquals("check coverage metric", Metric.COVERAGE, coverage);

        Metric testExecutionTime = Metric.getMetric("test_execution_time");
        assertEquals("check test_execution_time metric", Metric.TEST_EXECUTION_TIME, testExecutionTime);
    }
}
