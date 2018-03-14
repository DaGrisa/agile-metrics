package at.grisa.agilemetrics.producer.bitbucket;

import at.grisa.agilemetrics.producer.bitbucket.restentities.Commit;
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

public class BitBucketRestClientCommitsTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Commit[] commits;

    @Before
    public void loadCommitsFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("bitbucket/commits.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects/PRJ/repos/REPO/commits")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        String projectKey = "PRJ";
        String repositorySlug = "REPO";
        BitBucketRestClient client = new BitBucketRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        commits = client.getCommits(projectKey, repositorySlug);
    }

    @Test
    public void countCommits() {
        assertEquals("1 commit in total", 1, commits.length);
    }

    @Test
    public void checkData() {
        Commit commit = commits[0];
        assertEquals("check commit id", "def0123abcdef4567abcdef8987abcdef6543abc", commit.getId());
        assertEquals("check commit message", "More work on feature 1", commit.getMessage());
    }
}