package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Project;
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

public class BitBucketServerRestClientProjectsTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Project> projects;

    @Before
    public void loadProjectsFromMockServer() throws IOException, URISyntaxException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("bitbucket/projects.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        BitBucketServerRestClient client = new BitBucketServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        projects = client.getProjects();
    }

    @Test
    public void countRepos() {
        assertEquals("1 project in total", 1, projects.size());
    }

    @Test
    public void checkData() {
        Project project = projects.iterator().next();
        assertEquals("check project id", new Long(1), project.getId());
        assertEquals("check project key", "PRJ", project.getKey());
        assertEquals("check project name", "My Cool Project", project.getName());
    }
}