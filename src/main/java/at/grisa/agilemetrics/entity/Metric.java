package at.grisa.agilemetrics.entity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public final class Metric {
    public final Double value;
    public final String name;
    public final Map meta;
    public final LocalDate date;
    public final Set<String> tags;

    public Metric(Double value, String name, Map<String, String> meta) {
        this.value = value;
        this.name = name;
        this.meta = meta;
        this.date = LocalDate.now();
        tags = null;
    }

    public Metric(Double value, String name, Map<String, String> meta, Set<String> tags) {
        this.value = value;
        this.name = name;
        this.meta = meta;
        this.tags = tags;
        this.date = LocalDate.now();
    }
}
