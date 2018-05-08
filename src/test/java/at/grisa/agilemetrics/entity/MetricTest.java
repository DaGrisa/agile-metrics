package at.grisa.agilemetrics.entity;

import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Sprint;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

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
        HashSet<String> tags = new HashSet<>();
        tags.add("some");
        tags.add("tags");
        Metric issueVolumeMetric = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta, tags);

        HashMap<String, String> issueVolumeMeta2 = new HashMap<>();
        issueVolumeMeta2.put("sprint", board.getName());
        issueVolumeMeta2.put("board", sprint.getName());
        HashSet<String> tags2 = new HashSet<>();
        tags2.add("some");
        tags2.add("tags");
        Metric issueVolumeMetric2 = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta2, tags2);

        assertEquals("hashcode on metric works fine", issueVolumeMetric.hashCode(), issueVolumeMetric2.hashCode());
    }
}
