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

public class JiraSoftwareServerRestClientSprintIssuesTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Issue> issues;

    @Before
    public void loadIssuesFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/issues.js").toURI())));

        Long boardId = 1234L;
        Long sprintId = 5678L;
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/agile/1.0/board/" + boardId + "/sprint/" + sprintId + "/issue")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        issues = client.getSprintIssues(boardId, sprintId);
    }

    @Test
    public void countIssues() {
        assertEquals("2 issues in total", 2, issues.size());
    }
}
