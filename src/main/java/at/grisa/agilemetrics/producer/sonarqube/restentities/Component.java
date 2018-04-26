package at.grisa.agilemetrics.producer.sonarqube.restentities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {
    private String organization;
    private String key;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    private Instant lastAnalysisDate;
    private Measure[] measures;

    public Component() {
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastAnalysisDate() {
        return lastAnalysisDate;
    }

    public void setLastAnalysisDate(Instant lastAnalysisDate) {
        this.lastAnalysisDate = lastAnalysisDate;
    }

    public Measure[] getMeasures() {
        return measures;
    }

    public void setMeasures(Measure[] measures) {
        this.measures = measures;
    }
}