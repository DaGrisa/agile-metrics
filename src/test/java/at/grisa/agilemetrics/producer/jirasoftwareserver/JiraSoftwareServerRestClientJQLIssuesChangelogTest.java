package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.History;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.HistoryItem;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Issue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JiraSoftwareServerRestClientJQLIssuesChangelogTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Issue issue;

    @Before
    public void loadIssueFromMockServer() throws URISyntaxException, IOException {
        Long issueId = 1234L;
        String issueChangelogResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/issueChangelog.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/2/issue/" + issueId)
                        .withQueryStringParameter("expand", "changelog")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(issueChangelogResponse)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        issue = client.getIssue(issueId, new QueryParam("expand", "changelog"));
    }

    @Test
    public void checkData() {
        assertNotNull("check changelog not null", issue.getChangelog());
        assertEquals("check changelog histories count", 5, issue.getChangelog().getHistories().length);

        History history = issue.getChangelog().getHistories()[0];
        assertEquals("check issue created date", OffsetDateTime.parse("2017-05-18T20:49:58.000+0200", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant(), history.getCreated());

        HistoryItem historyItem = history.getItems()[0];
        assertEquals("check issue changelog history item field", "status", historyItem.getField());
        assertEquals("check issue changelog history item from string", "New", historyItem.getFromString());
        assertEquals("check issue changelog history item to string", "in Progress", historyItem.getToString());
    }
}
