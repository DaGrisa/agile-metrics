package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Sprint;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, PropertyManager.class, JiraSoftwareServerProducerMockConfiguration.class, ApplicationConfig.class})
public class JiraSoftwareServerProducerIssueVolumeTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
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

        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getActiveSprint(board.getId())).thenReturn(sprint);
        when(jiraSoftwareServerRestClient.getSprintIssuesCount(boardId, sprintId)).thenReturn(issueVolume);
    }

    @Test
    public void produceIssueVolumeTest() {
        jiraSoftwareServerProducer.produceIssueVolume();

        HashMap<String, String> issueVolumeMeta = new HashMap<>();
        issueVolumeMeta.put("sprint", sprint.getName());
        issueVolumeMeta.put("board", board.getName());
        Metric issueVolumeMetric = new Metric(issueVolume.doubleValue(), "Issue Volume", issueVolumeMeta);

        verify(metricQueue).enqueueMetric(issueVolumeMetric);
    }
}
