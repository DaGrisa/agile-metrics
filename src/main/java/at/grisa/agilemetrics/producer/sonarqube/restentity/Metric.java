package at.grisa.agilemetrics.producer.sonarqube.restentity;

public enum Metric {
    COVERAGE("coverage");

    private String value;

    Metric(String value) {
        this.value = value;
    }

    public static Metric getMetric(String value) {
        switch (value) {
            case "coverage":
                return COVERAGE;
            default:
                throw new IllegalArgumentException("unknown value " + value + " for the Metric enum");
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
