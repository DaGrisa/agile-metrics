package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Fields;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Issue;
import at.grisa.agilemetrics.util.CredentialManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, JiraSoftwareServerProducerMockConfiguration.class})
public class JiraSoftwareServerProducerLeadTimeTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private MetricQueue metricQueue;

    private Board board;
    private Issue issue1;
    private Issue issue2;
    private Issue issue3;

    private final Long boardId = 123L;
    private final String boardJql = "boardJQL";

    @Before
    public void createMocks() {
        board = new Board();
        board.setId(boardId);
        board.setName("boardname");

        Fields fields1 = new Fields();
        Instant created1 = OffsetDateTime.parse("2017-11-06T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant();
        fields1.setCreated(created1.atZone(ZoneId.systemDefault()));
        Instant resolution1 = OffsetDateTime.parse("2017-11-08T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant();
        fields1.setResolutiondate(resolution1.atZone(ZoneId.systemDefault()));

        issue1 = new Issue();
        issue1.setKey("issue1");
        issue1.setFields(fields1);

        Fields fields2 = new Fields();
        Instant created2 = OffsetDateTime.parse("2017-11-05T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant();
        fields2.setCreated(created2.atZone(ZoneId.systemDefault()));
        Instant resolution2 = OffsetDateTime.parse("2017-11-09T18:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant();
        fields2.setResolutiondate(resolution2.atZone(ZoneId.systemDefault()));

        issue2 = new Issue();
        issue2.setKey("issue2");
        issue2.setFields(fields2);

        Fields fields3 = new Fields();
        Instant created3 = OffsetDateTime.parse("2017-11-06T09:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant();
        fields3.setCreated(created3.atZone(ZoneId.systemDefault()));
        Instant resolution3 = OffsetDateTime.parse("2017-11-07T17:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant();
        fields3.setResolutiondate(resolution3.atZone(ZoneId.systemDefault()));

        issue3 = new Issue();
        issue3.setKey("issue3");
        issue3.setFields(fields3);

        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getScrumBoardJQLFilter(boardId)).thenReturn(boardJql);
        when(jiraSoftwareServerRestClient.getIssuesByJQL("resolutiondate > -1d AND " + boardJql)).thenReturn(Arrays.asList(issue1, issue2, issue3));
    }

    @Test
    public void produceLeadTimeTest() {
        jiraSoftwareServerProducer.produceLeadTime();

        HashMap<String, String> meta = new HashMap<>();
        meta.put("issue", issue1.getKey());
        meta.put("scrum-board", board.getName());
        Metric leadTime1 = new Metric(2.0, "Lead Time", meta);

        HashMap<String, String> meta2 = new HashMap<>();
        meta2.put("issue", issue2.getKey());
        meta2.put("scrum-board", board.getName());
        Metric leadTime2 = new Metric(4.0, "Lead Time", meta2);

        HashMap<String, String> meta3 = new HashMap<>();
        meta3.put("issue", issue3.getKey());
        meta3.put("scrum-board", board.getName());
        Metric leadTime3 = new Metric(1.0, "Lead Time", meta3);


        verify(metricQueue).enqueueMetric(leadTime1);
        verify(metricQueue).enqueueMetric(leadTime2);
        verify(metricQueue).enqueueMetric(leadTime3);
    }
}
