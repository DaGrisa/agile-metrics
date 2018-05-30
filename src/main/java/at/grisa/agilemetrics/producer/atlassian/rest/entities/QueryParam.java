package at.grisa.agilemetrics.producer.atlassian.rest.entities;

import java.util.Objects;

public class QueryParam {
    public final String name;
    public final Object value;

    public QueryParam(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "QueryParam{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryParam that = (QueryParam) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
