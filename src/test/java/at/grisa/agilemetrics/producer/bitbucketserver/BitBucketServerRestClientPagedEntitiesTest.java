package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Repository;
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
public class BitBucketServerRestClientPagedEntitiesTest {
    @Autowired
    private BitBucketServerRestClient client;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
    private MockServerClient mockServerClient;
    private Collection<Repository> repositories;

    @Before
    public void loadReposFromMockServer() throws URISyntaxException, IOException {
        String responseBodyPage1 = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("bitbucket/paged-entities-1.js").toURI())));
        String responseBodyPage2 = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("bitbucket/paged-entities-2.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects/PAGED/repos")
                        .withQueryStringParameter("start", "0")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBodyPage1)
                );

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects/PAGED/repos")
                        .withQueryStringParameter("start", "3")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBodyPage2)
                );

        String projectKey = "PAGED";
        repositories = client.getRepositories(projectKey);
    }

    @Test
    public void countRepos() {
        assertEquals("5 values in total", 5, repositories.size());
    }

}