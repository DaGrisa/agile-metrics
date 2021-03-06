package at.grisa.agilemetrics.producer.sonarqube.restentity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {
    private String organization;
    private String key;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    private ZonedDateTime lastAnalysisDate;
    private Measure[] measures;

    public Component() {
        // default constructor
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

    public ZonedDateTime getLastAnalysisDate() {
        return lastAnalysisDate;
    }

    public void setLastAnalysisDate(ZonedDateTime lastAnalysisDate) {
        this.lastAnalysisDate = lastAnalysisDate;
    }

    public Measure[] getMeasures() {
        return measures;
    }

    public void setMeasures(Measure[] measures) {
        this.measures = measures;
    }
}