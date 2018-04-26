package at.grisa.agilemetrics.producer.jirasoftwareserver;

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
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JiraSoftwareServerRestClientJQLIssuesLabelsTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Issue> issues;

    @Before
    public void loadIssueLabelsFromMockServer() throws URISyntaxException, IOException {
        String jql = "project = team1 ORDER BY create ASC";
        String issueLabelsResponse = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/issueLabels.js").toURI())));

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
                                .withBody(issueLabelsResponse)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        issues = client.getIssuesByJQL(jql);
    }

    @Test
    public void countIssues() {
        assertEquals("1 issue in total", 1, issues.size());
    }

    @Test
    public void checkData() {
        Issue issue = issues.iterator().next();
        assertEquals("check issue id", new Long(1), issue.getId());
        assertEquals("check issue key", "ISSUE-1", issue.getKey());
        assertEquals("check issue labels size", 3, issue.getFields().getLabels().length);
    }
}
