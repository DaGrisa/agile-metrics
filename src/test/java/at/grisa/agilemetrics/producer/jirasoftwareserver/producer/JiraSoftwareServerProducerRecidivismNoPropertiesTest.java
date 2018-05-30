package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Board;
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

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, PropertyManager.class, JiraSoftwareServerProducerMockConfiguration.class, ApplicationConfig.class})
public class JiraSoftwareServerProducerRecidivismNoPropertiesTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private MetricQueue metricQueue;

    private Board board;
    private Issue issue1;
    private Issue issue2;

    private final Long boardId = 123L;
    private final String boardJql = "boardJQL ORDER BY boing";

    @Before
    public void createMocks() {
        board = new Board();
        board.setId(boardId);
        board.setName("boardname");

        issue1 = new Issue();
        issue1.setKey("issue1");

        issue2 = new Issue();
        issue2.setKey("issue2");

        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getScrumBoardJQLFilter(boardId)).thenReturn(boardJql);
        when(jiraSoftwareServerRestClient.getIssuesByJQL("resolutiondate > -1d AND (boardjql) order by boing")).thenReturn(Arrays.asList(issue1, issue2));
    }

    @Test(expected = IllegalStateException.class)
    public void produceLeadTimeTest() {
        jiraSoftwareServerProducer.produceRecidivism();
    }
}
