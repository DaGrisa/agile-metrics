package at.grisa.agilemetrics.producer.bitbucketserver.restclient;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerRestClient;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Project;
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
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitBucketServerRestClient.class, CredentialManager.class, ApplicationConfig.class})
@TestPropertySource("classpath:bitbucket-test.properties")
public class BitBucketServerRestClientProjectsTest {
    @Autowired
    private BitBucketServerRestClient client;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
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