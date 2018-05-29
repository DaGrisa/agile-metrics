package at.grisa.agilemetrics.producer.atlassian.rest.entities;

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
}
