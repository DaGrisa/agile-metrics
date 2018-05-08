package at.grisa.agilemetrics.producer.jirasoftwareserver.restclient;

import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Issue;
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

public class JiraSoftwareServerRestClientSprintIssuesStatusTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Issue> issues;

    @Before
    public void loadIssuesStatusFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/issuesStatus.js").toURI())));

        Long boardId = 1234L;
        Long sprintId = 5678L;
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/agile/1.0/board/" + boardId + "/sprint/" + sprintId + "/issue")
                        .withQueryStringParameter("fields", "status")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        issues = client.getSprintIssuesStatus(boardId, sprintId);
    }

    @Test
    public void countIssues() {
        assertEquals("10 issues in total", 10, issues.size());
    }

    @Test
    public void checkData() {
        Issue issue = issues.iterator().next();
        assertEquals("check issue id", new Long(10001), issue.getId());
        assertEquals("check issue key", "ISSUE-1", issue.getKey());
        assertEquals("check issue status name", "Released", issue.getFields().getStatus().getName());
        assertEquals("check issue status category name", "Done", issue.getFields().getStatus().getStatusCategory().getName());
    }
}
