package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Sprint;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.VelocityReport;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.VelocityStats;
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

public class JiraSoftwareServerRestClientVelocityReportGreenhopperTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private VelocityReport velocityReport;

    @Before
    public void loadVelocityReportFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/velocityReport.js").toURI())));

        Long rapidviewId = 1234L;
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/greenhopper/1.0/rapid/charts/velocity")
                        .withQueryStringParameter("rapidViewId", rapidviewId.toString())
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        velocityReport = client.getVelocityReportGreenhopper(rapidviewId);
    }

    @Test
    public void checkData() {
        assertEquals("check velocity report sprints count", 3, velocityReport.getSprints().length);
        Sprint sprint = velocityReport.getSprints()[0];
        assertEquals("check sprint id", new Long(1), sprint.getId());
        assertEquals("check sprint sequence", new Integer(1), sprint.getSequence());
        assertEquals("check sprint name", "SPRINT-1", sprint.getName());
        assertEquals("check sprint state", "CLOSED", sprint.getState());
        assertEquals("check sprint goal", "Sprint 1 goal", sprint.getGoal());

        assertEquals("check velocity report stats count", 3, velocityReport.getVelocityStatEntries().size());
        VelocityStats velocityStats = velocityReport.getVelocityStatEntries().get("1");
        assertEquals("check velocity stats completed", new Integer(38), velocityStats.getCompleted().getValue());
        assertEquals("check velocity stats estimated", new Integer(42), velocityStats.getEstimated().getValue());
    }
}
