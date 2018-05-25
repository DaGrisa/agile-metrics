package at.grisa.agilemetrics.producer.sonarqube.restentity;

public enum Metric {
    COVERAGE("coverage"),
    TEST_EXECUTION_TIME("test_execution_time");

    private String value;

    Metric(String value) {
        this.value = value;
    }

    public static Metric getMetric(String value) {
        switch (value) {
            case "coverage":
                return Metric.COVERAGE;
            case "test_execution_time":
                return Metric.TEST_EXECUTION_TIME;
            default:
                throw new IllegalArgumentException("unknown value " + value + " for the Metric enum");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
