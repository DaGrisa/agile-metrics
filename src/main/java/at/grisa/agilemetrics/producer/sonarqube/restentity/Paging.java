package at.grisa.agilemetrics.producer.sonarqube.restentity;

public class Paging {
    private Integer pageIndex;
    private Integer pageSize;
    private Integer total;

    public Paging() {
        // default constructor
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public boolean hasNextPage() {
        Integer resutls = pageIndex * pageSize;
        return resutls < total;
    }
}
