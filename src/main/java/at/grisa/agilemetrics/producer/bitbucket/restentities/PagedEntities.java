package at.grisa.agilemetrics.producer.bitbucket.restentities;

public class PagedEntities<T> {
    private Boolean isLastPage;
    private Long nextPageStart;
    private T[] values;

    public PagedEntities() {
    }

    public Boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(Boolean lastPage) {
        isLastPage = lastPage;
    }

    public Long getNextPageStart() {
        return nextPageStart;
    }

    public void setNextPageStart(Long nextPageStart) {
        this.nextPageStart = nextPageStart;
    }

    public T[] getValues() {
        return values;
    }

    public void setValues(T[] values) {
        this.values = values;
    }
}
