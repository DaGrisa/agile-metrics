package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Fields;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Issue;
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
import java.util.HashSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, PropertyManager.class, JiraSoftwareServerProducerMockConfiguration.class, ApplicationConfig.class})
public class JiraSoftwareServerProducerIssueLabelsTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private MetricQueue metricQueue;

    private Board board;
    private Issue issue1;

    private final Long boardId = 123L;
    private final String boardJql = "boardJQL ORDER BY boing";
    private final String[] labels = new String[]{"one", "two", "three", "äáß123 what a #label"};

    @Before
    public void createMocks() {
        board = new Board();
        board.setId(boardId);
        board.setName("boardname");

        Fields fields = new Fields();
        fields.setLabels(labels);

        issue1 = new Issue();
        issue1.setKey("issue1");
        issue1.setFields(fields);

        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getScrumBoardJQLFilter(boardId)).thenReturn(boardJql);
        when(jiraSoftwareServerRestClient.getIssuesByJQL("resolutiondate > -1d AND (boardjql) order by boing")).thenReturn(Arrays.asList(issue1));
    }

    @Test
    public void produceIssueLabelsTest() {
        jiraSoftwareServerProducer.produceIssueLabels();

        HashMap<String, String> meta = new HashMap<>();
        meta.put("issue", issue1.getKey());
        meta.put("board", board.getName());
        Metric issueLabels = new Metric(4.0, "Issue Labels", meta, new HashSet<>(Arrays.asList(labels)));

        verify(metricQueue).enqueueMetric(issueLabels);
    }
}
