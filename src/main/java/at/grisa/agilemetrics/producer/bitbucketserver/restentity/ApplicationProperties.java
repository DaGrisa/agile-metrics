package at.grisa.agilemetrics.producer.bitbucketserver.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationProperties {
    private String version;
    private String buildNumber;
    private String buildDate;
    private String displayName;

    public ApplicationProperties() {
        // default constructor
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEmpty() {
        return version == null && buildNumber == null && buildDate == null && displayName == null;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "version='" + version + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                ", buildDate='" + buildDate + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
