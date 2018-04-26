package at.grisa.agilemetrics.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Velocity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String team;
    private String sprint;
    private String goal;
    private Integer estimated;
    private Integer completed;

    public Velocity() {
    }

    public Velocity(String team, String sprint, String goal, Integer estimated, Integer completed) {
        this.team = team;
        this.sprint = sprint;
        this.goal = goal;
        this.estimated = estimated;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getSprint() {
        return sprint;
    }

    public void setSprint(String sprint) {
        this.sprint = sprint;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getEstimated() {
        return estimated;
    }

    public void setEstimated(Integer estimated) {
        this.estimated = estimated;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }
}
