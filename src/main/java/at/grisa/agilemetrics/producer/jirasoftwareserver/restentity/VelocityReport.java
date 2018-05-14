package at.grisa.agilemetrics.producer.jirasoftwareserver.restentity;

import java.util.Map;

public class VelocityReport {
    private Sprint[] sprints;
    private Map<String, VelocityStats> velocityStatEntries;

    public VelocityReport() {
        // default constructor
    }

    public Sprint[] getSprints() {
        return sprints;
    }

    public void setSprints(Sprint[] sprints) {
        this.sprints = sprints;
    }

    public Map<String, VelocityStats> getVelocityStatEntries() {
        return velocityStatEntries;
    }

    public void setVelocityStatEntries(Map<String, VelocityStats> velocityStatEntries) {
        this.velocityStatEntries = velocityStatEntries;
    }
}
