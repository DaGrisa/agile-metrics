package at.grisa.agilemetrics.entity;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Metric {
    public final Double value;
    public final String name;
    public final Map meta;
    public final ZonedDateTime date;
    public final Set<String> tags;

    public Metric(Double value, String name, Map<String, String> meta) {
        this.value = value;
        this.name = name;
        this.meta = meta;
        this.date = ZonedDateTime.now();
        tags = null;
    }

    public Metric(Double value, String name, Map<String, String> meta, Set<String> tags) {
        this.value = value;
        this.name = name;
        this.meta = meta;
        this.tags = tags;
        this.date = ZonedDateTime.now();
    }

    public Metric(Double value, String name, Map<String, String> meta, Set<String> tags, ZonedDateTime date) {
        this.value = value;
        this.name = name;
        this.meta = meta;
        this.tags = tags;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metric metric = (Metric) o;
        DateTimeFormatter compareFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
        return Objects.equals(value, metric.value) &&
                Objects.equals(name, metric.name) &&
                Objects.equals(meta, metric.meta) &&
                Objects.equals(compareFormatter.format(date), compareFormatter.format(metric.date)) &&
                Objects.equals(tags, metric.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, name, meta, date.format(DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss")), tags);
    }

    @Override
    public String toString() {
        return "Metric{" +
                "value=" + value +
                ", name='" + name + '\'' +
                ", meta=" + meta +
                ", date=" + date +
                ", tags=" + tags +
                '}';
    }
}
