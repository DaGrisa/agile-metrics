package at.grisa.agilemetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@JsonIgnoreProperties(ignoreUnknown = true)
final public class Measurement {
    public final String name;
    public final Double value;
    public final Date valueDate;
    public final Date created;
    public final String user;
    public final String project;
    public final HashSet<String> tags;

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
