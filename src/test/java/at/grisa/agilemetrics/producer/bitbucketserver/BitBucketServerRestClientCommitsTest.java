package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Commit;
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
public class BitBucketServerRestClientCommitsTest {
    @Autowired
    private BitBucketServerRestClient client;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
    private MockServerClient mockServerClient;
    private Collection<Commit> commits;

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
        commits = client.getCommits(projectKey, repositorySlug);
    }

    @Test
    public void countCommits() {
        assertEquals("1 commit in total", 1, commits.size());
    }

    @Test
    public void checkData() {
        Commit commit = commits.iterator().next();
        assertEquals("check commit id", "def0123abcdef4567abcdef8987abcdef6543abc", commit.getId());
        assertEquals("check commit message", "More work on feature 1", commit.getMessage());
    }
}