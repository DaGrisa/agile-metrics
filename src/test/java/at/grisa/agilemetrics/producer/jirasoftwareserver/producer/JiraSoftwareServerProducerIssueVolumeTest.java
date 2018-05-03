package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Sprint;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class JiraSoftwareServerProducerIssueVolumeTest {
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    private MetricQueue metricQueue;
    private Board board;
    private Sprint sprint;

    private final Long boardId = 123L;
    private final Long sprintId = 456L;
    private final Integer issueVolume = 5;

    @Before
    public void createMocks() {
        board = new Board();
        board.setId(boardId);
        board.setName("boardname");
        sprint = new Sprint();
        sprint.setId(sprintId);
        sprint.setName("sprintname");

        jiraSoftwareServerRestClient = mock(JiraSoftwareServerRestClient.class);
        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getActiveSprint(board.getId())).thenReturn(sprint);
        when(jiraSoftwareServerRestClient.getSprintIssuesCount(boardId, sprintId)).thenReturn(issueVolume);

        metricQueue = mock(MetricQueue.class);
    }

    @Test
    public void produceIssueVolumeTest() {
        JiraSoftwareServerProducer jiraSoftwareServerProducer = new JiraSoftwareServerProducer();
        jiraSoftwareServerProducer.produceIssueVolume(metricQueue, jiraSoftwareServerRestClient);

        HashMap<String, String> issueVolumeMeta = new HashMap<>();
        issueVolumeMeta.put("sprint", sprint.getName());
        issueVolumeMeta.put("board", board.getName());
        Metric issueVolumeMetric = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta);

        verify(metricQueue).enqueueMetric(issueVolumeMetric);
    }
}
