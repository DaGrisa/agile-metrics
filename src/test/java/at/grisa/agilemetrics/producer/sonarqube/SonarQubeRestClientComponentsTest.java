package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.producer.sonarqube.restentity.Component;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class SonarQubeRestClientComponentsTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Component> components;

    @Before
    public void loadComponentsFromMockServer() throws URISyntaxException, IOException {
        String responseBodyPart1 = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("sonarqube/componentsPart1.js").toURI())));
        String responseBodyPart2 = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("sonarqube/componentsPart2.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/api/projects/search")
                        .withQueryStringParameter("qualifiers", "TRK")
                        .withQueryStringParameter("p", "1")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBodyPart1)
                );

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/api/projects/search")
                        .withQueryStringParameter("qualifiers", "TRK")
                        .withQueryStringParameter("p", "2")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBodyPart2)
                );

        SonarQubeRestClient client = new SonarQubeRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        components = client.getComponents();
    }

    @Test
    public void countIssues() {
        assertEquals("5 components in total", 5, components.size());
    }

    @Test
    public void checkData() {
        Component component = components.iterator().next();
        assertEquals("check component key", "com.company:project", component.getKey());
        assertEquals("check component name", "project", component.getName());
        assertEquals("check component organization", "default-organization", component.getOrganization());
        assertEquals("check component date", OffsetDateTime.parse("2018-03-05T09:56:35+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")).toInstant().atZone(ZoneId.of("UTC")), component.getLastAnalysisDate());
    }
}
