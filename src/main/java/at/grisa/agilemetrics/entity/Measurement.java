package at.grisa.agilemetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Measurement {
    private String name;
    private Double value;
    private Date valueDate;
    private Date created;
    private String user;
    private String project;
    private HashSet<String> tags;

    public Measurement(String name, String project, String user, Double value, Date valueDate, Date created, String... tags) {
        this.name = name;
        this.project = project;
        this.user = user;
        this.value = value;
        this.valueDate = valueDate;
        this.created = created;
        this.tags = new HashSet<>();
        this.tags.addAll(Arrays.asList(tags));
    }
}
