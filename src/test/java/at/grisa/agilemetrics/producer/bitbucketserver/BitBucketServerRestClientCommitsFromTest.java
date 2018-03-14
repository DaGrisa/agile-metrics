package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Commit;
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
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class BitBucketServerRestClientCommitsFromTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Commit> commits;

    @Before
    public void loadCommitsFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("bitbucket/commits-from.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects/PRJ/repos/FROM/commits")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        String projectKey = "PRJ";
        String repositorySlug = "FROM";
        BitBucketServerRestClient client = new BitBucketServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        Date from = new Date();
        from.setTime(1518587500000L);
        commits = client.getCommits(projectKey, repositorySlug, from);
    }

    @Test
    public void countCommits() {
        assertEquals("3 commits from Wed Feb 14 2018 05:51:40", 3, commits.size());
    }
}