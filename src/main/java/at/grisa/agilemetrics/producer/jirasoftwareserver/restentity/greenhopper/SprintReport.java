package at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SprintReport {
    private Contents contents;

    public SprintReport() {
        // default constructor
    }

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }
}
