package at.grisa.agilemetrics.producer.jirasoftwareserver.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardConfiguration {
    private Filter filter;

    public BoardConfiguration() {
        // default constructor
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
