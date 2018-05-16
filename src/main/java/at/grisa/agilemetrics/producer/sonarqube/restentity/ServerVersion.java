package at.grisa.agilemetrics.producer.sonarqube.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerVersion {
    private String version;

    public ServerVersion() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isEmpty() {
        return version == null;
    }
}
