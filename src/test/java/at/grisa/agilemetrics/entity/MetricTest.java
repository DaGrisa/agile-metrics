package at.grisa.agilemetrics.entity;

import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Sprint;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricTest {
    private Board board;
    private Sprint sprint;

    private final Integer issueVolume = 5;

    @Before
    public void createMocks() {
        board = new Board();
        board.setName("boardname");
        sprint = new Sprint();
        sprint.setName("sprintname");
    }

    @Test
    public void metricEquals() {
        HashMap<String, String> issueVolumeMeta = new HashMap<>();
        issueVolumeMeta.put("sprint", board.getName());
        issueVolumeMeta.put("board", sprint.getName());
        Metric issueVolumeMetric = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta);

        HashMap<String, String> issueVolumeMeta2 = new HashMap<>();
        issueVolumeMeta2.put("sprint", board.getName());
        issueVolumeMeta2.put("board", sprint.getName());
        Metric issueVolumeMetric2 = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta2);

        assertTrue("equals on metric works fine", issueVolumeMetric.equals(issueVolumeMetric2));
    }

    @Test
    public void metricHashCode() {
        HashMap<String, String> issueVolumeMeta = new HashMap<>();
        issueVolumeMeta.put("sprint", board.getName());
        issueVolumeMeta.put("board", sprint.getName());
        Metric issueVolumeMetric = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta);

        HashMap<String, String> issueVolumeMeta2 = new HashMap<>();
        issueVolumeMeta2.put("sprint", board.getName());
        issueVolumeMeta2.put("board", sprint.getName());
        Metric issueVolumeMetric2 = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta2);

        assertEquals("hashcode on metric works fine", issueVolumeMetric.hashCode(), issueVolumeMetric2.hashCode());
    }
}
