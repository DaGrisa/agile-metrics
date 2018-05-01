package at.grisa.agilemetrics.producer.jirasoftwareserver.restclient;

import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
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
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JiraSoftwareServerRestClientJQLIssuesTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Issue> issues;

    @Before
    public void loadIssuesFromMockServer() throws URISyntaxException, IOException {
        Long boardId = 1234L;
        Long filterId = 12345L;
        String boardConfigurationResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/boardConfiguration.js").toURI())));
        String filterResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/filter.js").toURI())));
        String issuesJQLResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/issuesJQL.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/agile/1.0/board/" + boardId + "/configuration")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(boardConfigurationResponse)
                );

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/2/filter/" + filterId)
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(filterResponse)
                );

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/2/search")
                        .withQueryStringParameter("jql", "project = team1 ORDER BY create ASC")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(issuesJQLResponse)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        String jql = client.getScrumBoardJQLFilter(boardId);
        issues = client.getIssuesByJQL(jql);
    }

    @Test
    public void countIssues() {
        assertEquals("4 issues in total", 4, issues.size());
    }

    @Test
    public void checkData() {
        Issue issue = issues.iterator().next();
        assertEquals("check issue id", new Long(1), issue.getId());
        assertEquals("check issue created date", OffsetDateTime.parse("2017-11-06T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant(), issue.getFields().getCreated());
        assertEquals("check issue resolution date", OffsetDateTime.parse("2018-04-24T09:46:16.000+0200", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toInstant(), issue.getFields().getResolutiondate());
    }
}
