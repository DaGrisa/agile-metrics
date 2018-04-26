package at.grisa.agilemetrics.producer.jirasoftwareserver.restentities;

public class VelocityStats {
    private Long sprintId;
    private Value estimated;
    private Value completed;

    public VelocityStats() {
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Value getEstimated() {
        return estimated;
    }

    public void setEstimated(Value estimated) {
        this.estimated = estimated;
    }

    public Value getCompleted() {
        return completed;
    }

    public void setCompleted(Value completed) {
        this.completed = completed;
    }
}
