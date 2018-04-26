package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.producer.sonarqube.restentities.Measure;
import at.grisa.agilemetrics.producer.sonarqube.restentities.Metric;
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

public class SonarQubeRestClientMeasuresTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Measure> measures;

    @Before
    public void loadComponentFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("sonarqube/component.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/api/measures/component")
                        .withQueryStringParameter("component", "component")
                        .withQueryStringParameter("metricKeys", "coverage")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        SonarQubeRestClient client = new SonarQubeRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        measures = client.getMeasures("component", Metric.COVERAGE);
    }

    @Test
    public void countIssues() {
        assertEquals("1 measure in total", 1, measures.size());
    }

    @Test
    public void checkData() {
        Measure measure = measures.iterator().next();
        assertEquals("check measure metric", "coverage", measure.getMetric());
        assertEquals("check measure value", new Double(95.3), measure.getValue());
    }
}
