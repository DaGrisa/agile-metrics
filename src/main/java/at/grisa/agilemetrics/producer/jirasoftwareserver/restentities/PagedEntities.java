package at.grisa.agilemetrics.producer.jirasoftwareserver.restentities;

public class PagedEntities<T> {
    private Boolean isLast;
    private Integer startAt;
    private Integer maxResults;
    private Integer total;

    private T[] values;
    private Issue[] issues;

    public PagedEntities() {
    }

    public Boolean getLast() {
        return isLast;
    }

    public void setLast(Boolean last) {
        isLast = last;
    }

    public Integer getStartAt() {
        return startAt;
    }

    public void setStartAt(Integer startAt) {
        this.startAt = startAt;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public T[] getValues() {
        return values;
    }

    public void setValues(T[] values) {
        this.values = values;
    }

    public Issue[] getIssues() {
        return issues;
    }

    public void setIssues(Issue[] issues) {
        this.issues = issues;
    }
}
