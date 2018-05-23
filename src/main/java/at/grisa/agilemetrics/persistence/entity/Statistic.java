package at.grisa.agilemetrics.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer metricsCount;
    private ZonedDateTime lastRun;

    public Statistic() {
        // default constructor
    }

    public Statistic(Integer metricsCount, ZonedDateTime lastRun) {
        this.metricsCount = metricsCount;
        this.lastRun = lastRun;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMetricsCount() {
        return metricsCount;
    }

    public void setMetricsCount(Integer metricsCount) {
        this.metricsCount = metricsCount;
    }

    public ZonedDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(ZonedDateTime lastRun) {
        this.lastRun = lastRun;
    }
}
