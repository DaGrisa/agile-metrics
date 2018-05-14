package at.grisa.agilemetrics.producer.sonarqube.restentity;

public class ComponentSearch {
    private Paging paging;
    private Component[] components;

    public ComponentSearch() {
        // default constructor
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public Component[] getComponents() {
        return components;
    }

    public void setComponents(Component[] components) {
        this.components = components;
    }
}
