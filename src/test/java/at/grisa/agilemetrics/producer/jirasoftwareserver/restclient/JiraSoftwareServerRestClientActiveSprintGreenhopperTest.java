package at.grisa.agilemetrics.producer.jirasoftwareserver.restclient;

import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.Sprint;
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

public class JiraSoftwareServerRestClientActiveSprintGreenhopperTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Sprint sprint;
    private final long sprintId = 1234L;

    @Before
    public void loadIssuesFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/sprintsGreenhopper.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/greenhopper/1.0/sprintquery/" + sprintId)
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );
    }

    @Test
    public void checkRestClient() {
        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        sprint = client.getActiveSprintGreenhopper(sprintId);

        assertEquals("check rapidview id", new Long(4), sprint.getId());
        assertEquals("check rapidview name", "Sprint 4", sprint.getName());
        assertEquals("check rapidview sprint support", "ACTIVE", sprint.getState());
    }
}
