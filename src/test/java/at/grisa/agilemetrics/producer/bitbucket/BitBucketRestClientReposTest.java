package at.grisa.agilemetrics.producer.bitbucket;

import at.grisa.agilemetrics.producer.bitbucket.restentities.Repo;
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

public class BitBucketRestClientReposTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Repo[] repos;

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
        BitBucketRestClient client = new BitBucketRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        repos = client.getRepos(projectKey);
    }

    @Test
    public void countRepos() {
        assertEquals("1 repo in total", repos.length, 1);
    }

    @Test
    public void checkData() {
        Repo repo = repos[0];
        assertEquals("check repo id", repo.getId(), new Long(1));
        assertEquals("check repo slug", repo.getSlug(), "my-repo");
        assertEquals("check repo name", repo.getName(), "My repo");
    }
}