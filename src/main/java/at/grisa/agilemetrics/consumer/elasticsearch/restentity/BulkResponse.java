package at.grisa.agilemetrics.consumer.elasticsearch.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkResponse {
    private Boolean errors;

    public BulkResponse() {
    }

    public Boolean getErrors() {
        return errors;
    }

    public void setErrors(Boolean errors) {
        this.errors = errors;
    }
}
