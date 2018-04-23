package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Sprint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JiraSoftwareServerRestClientActiveSprintTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Sprint sprint;

    @Before
    public void loadSprintFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/sprints.js").toURI())));

        Long boardId = 1234L;
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/agile/1.0/board/" + boardId + "/sprint")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        sprint = client.getActiveSprint(boardId);
    }

    @Test
    public void checkData() {
        assertEquals("check sprint id", new Long(58), sprint.getId());
        assertEquals("check sprint name", "sprint 2", sprint.getName());
        assertEquals("check sprint type", "active", sprint.getState());
    }
}
