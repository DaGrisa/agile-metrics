package at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RapidView {
    private Long id;
    private String name;
    private Boolean sprintSupportEnabled;

    public RapidView() {
        // default constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSprintSupportEnabled() {
        return sprintSupportEnabled;
    }

    public void setSprintSupportEnabled(Boolean sprintSupportEnabled) {
        this.sprintSupportEnabled = sprintSupportEnabled;
    }
}
