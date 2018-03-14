package at.grisa.agilemetrics.producer.bitbucketserver.restentities;

public class PagedEntities<T> {
    private Boolean isLastPage;
    private Integer nextPageStart;
    private T[] values;

    public PagedEntities() {
    }

    public Boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(Boolean lastPage) {
        isLastPage = lastPage;
    }

    public Integer getNextPageStart() {
        return nextPageStart;
    }

    public void setNextPageStart(Integer nextPageStart) {
        this.nextPageStart = nextPageStart;
    }

    public T[] getValues() {
        return values;
    }

    public void setValues(T[] values) {
        this.values = values;
    }
}
