package at.grisa.agilemetrics.consumer.elasticsearch.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkResponse {
    private Boolean errors;
    private Map<String, Object>[] items;

    public BulkResponse() {
        // default constructor
    }

    public Boolean getErrors() {
        return errors;
    }

    public void setErrors(Boolean errors) {
        this.errors = errors;
    }

    public Map<String, Object>[] getItems() {
        return items;
    }

    public void setItems(Map<String, Object>[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "BulkResponse{" +
                "errors=" + errors +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
