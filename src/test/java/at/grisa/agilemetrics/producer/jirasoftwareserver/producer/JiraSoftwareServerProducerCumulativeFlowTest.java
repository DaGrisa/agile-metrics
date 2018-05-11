package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.*;
import at.grisa.agilemetrics.util.CredentialManager;
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
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, JiraSoftwareServerProducerMockConfiguration.class, ApplicationConfig.class})
public class JiraSoftwareServerProducerCumulativeFlowTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private MetricQueue metricQueue;

    private Board board;
    private Sprint sprint;
    private Issue issue1;
    private Issue issue2;
    private Issue issue3;
    private Status status;
    private Status status2;

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

        StatusCategory statusCategory = new StatusCategory();
        statusCategory.setName("status category");

        status = new Status();
        status.setName("status name");
        status.setStatusCategory(statusCategory);

        Fields fields = new Fields();
        fields.setStatus(status);

        issue1 = new Issue();
        issue1.setFields(fields);

        issue2 = new Issue();
        issue2.setFields(fields);

        StatusCategory statusCategory2 = new StatusCategory();
        statusCategory2.setName("status category 2");

        status2 = new Status();
        status2.setName("status name 2");
        status2.setStatusCategory(statusCategory2);

        Fields fields2 = new Fields();
        fields2.setStatus(status2);

        issue3 = new Issue();
        issue3.setFields(fields2);

        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getActiveSprint(board.getId())).thenReturn(sprint);
        when(jiraSoftwareServerRestClient.getSprintIssuesStatus(board.getId(), sprint.getId())).thenReturn(Arrays.asList(issue1, issue2, issue3));
    }

    @Test
    public void produceCumulativeFlowTest() {
        jiraSoftwareServerProducer.produceCumulativeFlow();

        HashMap<String, String> meta = new HashMap<>();
        meta.put("status", status.getName());
        meta.put("board", board.getName());
        meta.put("sprint", sprint.getName());
        Metric cumulativeFlow = new Metric(2.0, "Cumulative Flow - Status", meta);

        HashMap<String, String> meta2 = new HashMap<>();
        meta2.put("status", status2.getName());
        meta2.put("board", board.getName());
        meta2.put("sprint", sprint.getName());
        Metric cumulativeFlow2 = new Metric(1.0, "Cumulative Flow - Status", meta2);

        HashMap<String, String> meta3 = new HashMap<>();
        meta3.put("status-category", status.getStatusCategory().getName());
        meta3.put("board", board.getName());
        meta3.put("sprint", sprint.getName());
        Metric cumulativeFlow3 = new Metric(2.0, "Cumulative Flow - Status Category", meta3);

        HashMap<String, String> meta4 = new HashMap<>();
        meta4.put("status-category", status2.getStatusCategory().getName());
        meta4.put("board", board.getName());
        meta4.put("sprint", sprint.getName());
        Metric cumulativeFlow4 = new Metric(1.0, "Cumulative Flow - Status Category", meta4);

        verify(metricQueue).enqueueMetric(cumulativeFlow);
        verify(metricQueue).enqueueMetric(cumulativeFlow2);
        verify(metricQueue).enqueueMetric(cumulativeFlow3);
        verify(metricQueue).enqueueMetric(cumulativeFlow4);
    }
}
