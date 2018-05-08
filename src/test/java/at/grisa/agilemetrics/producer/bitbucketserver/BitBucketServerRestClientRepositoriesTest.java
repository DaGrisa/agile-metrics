package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Repository;
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

public class BitBucketServerRestClientRepositoriesTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Repository> repositories;

    @Before
    public void loadReposFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("bitbucket/repos.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects/PRJ/repos")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        String projectKey = "PRJ";
        BitBucketServerRestClient client = new BitBucketServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        repositories = client.getRepositories(projectKey);
    }

    @Test
    public void countRepos() {
        assertEquals("1 repo in total", 1, repositories.size());
    }

    @Test
    public void checkData() {
        Repository repository = repositories.iterator().next();
        assertEquals("check repository id", new Long(1), repository.getId());
        assertEquals("check repository slug", "my-repo", repository.getSlug());
        assertEquals("check repository name", "My repo", repository.getName());
    }
}