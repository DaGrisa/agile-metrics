package at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SprintReport {
    private Contents contents;

    public SprintReport() {
    }

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }
}
