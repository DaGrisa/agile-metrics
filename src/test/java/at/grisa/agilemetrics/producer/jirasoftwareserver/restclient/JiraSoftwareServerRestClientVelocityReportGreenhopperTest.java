package at.grisa.agilemetrics.producer.jirasoftwareserver.restclient;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Sprint;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.VelocityReport;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.VelocityStats;
import at.grisa.agilemetrics.util.CredentialManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerRestClient.class, CredentialManager.class, ApplicationConfig.class})
@TestPropertySource("classpath:jira-test.properties")
public class JiraSoftwareServerRestClientVelocityReportGreenhopperTest {
    @Autowired
    private JiraSoftwareServerRestClient client;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
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
