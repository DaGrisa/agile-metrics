package at.grisa.agilemetrics.consumer.elasticsearch.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterInfo {
    private String name;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("cluster_uuid")
    private String clusterUuid;

    public ClusterInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterUuid() {
        return clusterUuid;
    }

    public void setClusterUuid(String clusterUuid) {
        this.clusterUuid = clusterUuid;
    }

    public boolean isEmpty() {
        return name.isEmpty() && clusterName.isEmpty() && clusterUuid.isEmpty();
    }
}
