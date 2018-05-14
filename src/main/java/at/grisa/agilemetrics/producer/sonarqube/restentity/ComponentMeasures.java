package at.grisa.agilemetrics.producer.sonarqube.restentity;

public class ComponentMeasures {
    private Component component;

    public ComponentMeasures() {
        // default constructor
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }
}
