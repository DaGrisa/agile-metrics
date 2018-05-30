package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.*;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, PropertyManager.class, JiraSoftwareServerProducerMockConfiguration.class, ApplicationConfig.class})
@TestPropertySource("classpath:jira-test.properties")
public class JiraSoftwareServerProducerAcceptanceCriteriaVolatilityTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private MetricQueue metricQueue;

    private Board board;
    private Issue issue1;
    private Long issue1Id = 1L;
    private Issue issue2;
    private Long issue2Id = 2L;

    private final Long boardId = 123L;
    private final String boardJql = "boardJQL ORDER BY boing";

    @Before
    public void createMocks() {
        board = new Board();
        board.setId(boardId);
        board.setName("boardname");

        issue1 = getItemNoACVolatility();
        issue1.setId(issue1Id);
        issue2 = getItemACVolatility();
        issue2.setId(issue2Id);

        when(jiraSoftwareServerRestClient.getScrumBoards()).thenReturn(Arrays.asList(board));
        when(jiraSoftwareServerRestClient.getScrumBoardJQLFilter(boardId)).thenReturn(boardJql);
        when(jiraSoftwareServerRestClient.getIssuesByJQL("resolutiondate > -1d AND (boardjql) order by boing")).thenReturn(Arrays.asList(issue1, issue2));
        when(jiraSoftwareServerRestClient.getIssue(issue1Id, new QueryParam("expand", "changelog"))).thenReturn(issue1);
        when(jiraSoftwareServerRestClient.getIssue(issue2Id, new QueryParam("expand", "changelog"))).thenReturn(issue2);
    }

    private Issue getItemNoACVolatility() {
        History history1 = new History();
        history1.setCreated(OffsetDateTime.parse("2017-11-05T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem1 = new HistoryItem();
        historyItem1.setField("acceptance criteria");
        historyItem1.setFromString("tbd");
        historyItem1.setToString("final acceptance criteria");
        history1.setItems(new HistoryItem[]{historyItem1});

        ChangeLog changeLog = new ChangeLog();
        changeLog.setHistories(new History[]{history1});

        Issue issue = new Issue();
        issue.setKey("issue no ac volatility");
        issue.setChangelog(changeLog);

        return issue;
    }

    private Issue getItemACVolatility() {
        History history1 = new History();
        history1.setCreated(OffsetDateTime.parse("2017-11-05T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem1 = new HistoryItem();
        historyItem1.setField("acceptance criteria");
        historyItem1.setFromString("tbd");
        historyItem1.setToString("first idea");
        history1.setItems(new HistoryItem[]{historyItem1});

        History history2 = new History();
        history2.setCreated(OffsetDateTime.parse("2017-11-05T11:10:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem2 = new HistoryItem();
        historyItem2.setField("status");
        historyItem2.setFromString("To Do");
        historyItem2.setToString("In Progress");
        history2.setItems(new HistoryItem[]{historyItem2});

        History history3 = new History();
        history3.setCreated(OffsetDateTime.parse("2017-11-05T11:20:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem3 = new HistoryItem();
        historyItem3.setField("acceptance criteria");
        historyItem3.setFromString("first idea");
        historyItem3.setToString("first idea plus more ideas");
        history3.setItems(new HistoryItem[]{historyItem3});

        History history4 = new History();
        history4.setCreated(OffsetDateTime.parse("2017-11-05T11:30:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem4 = new HistoryItem();
        historyItem4.setField("acceptance criteria");
        historyItem4.setFromString("first idea plus more ideas");
        historyItem4.setToString("forget everything, all new");
        history4.setItems(new HistoryItem[]{historyItem4});

        History history5 = new History();
        history5.setCreated(OffsetDateTime.parse("2017-11-05T11:40:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem5 = new HistoryItem();
        historyItem5.setField("status");
        historyItem5.setFromString("In Progress");
        historyItem5.setToString("To Do");
        history5.setItems(new HistoryItem[]{historyItem5});

        History history6 = new History();
        history6.setCreated(OffsetDateTime.parse("2017-11-05T11:50:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime());
        HistoryItem historyItem6 = new HistoryItem();
        historyItem6.setField("acceptance criteria");
        historyItem6.setFromString("forget everything, all new");
        historyItem6.setToString("now we know for sure how i should work");
        history6.setItems(new HistoryItem[]{historyItem6});

        ChangeLog changeLog = new ChangeLog();
        changeLog.setHistories(new History[]{history1, history2, history3, history4, history5, history6});

        Issue issue = new Issue();
        issue.setKey("issue ac volatility");
        issue.setChangelog(changeLog);

        return issue;
    }

    @Test
    public void produceLeadTimeTest() {
        jiraSoftwareServerProducer.produceAcceptanceCriteriaVolatility();

        HashMap<String, String> meta = new HashMap<>();
        meta.put("issue", issue1.getKey());
        meta.put("board", board.getName());
        Metric leadTime1 = new Metric(1.0, "Acceptance Criteria Volatility", meta);

        HashMap<String, String> meta2 = new HashMap<>();
        meta2.put("issue", issue2.getKey());
        meta2.put("board", board.getName());
        Metric leadTime2 = new Metric(4.0, "Acceptance Criteria Volatility", meta2);

        verify(metricQueue).enqueueMetric(leadTime1);
        verify(metricQueue).enqueueMetric(leadTime2);
    }
}
