package at.grisa.agilemetrics.producer.bitbucket.restentities;

public class PagedEntities<T> {
    private Boolean isLastPage;
    private T[] values;

    public PagedEntities() {
    }

    public Boolean getLastPage() {
        return isLastPage;
    }

    public void setLastPage(Boolean lastPage) {
        isLastPage = lastPage;
    }

    public T[] getValues() {
        return values;
    }

    public void setValues(T[] values) {
        this.values = values;
    }
}
