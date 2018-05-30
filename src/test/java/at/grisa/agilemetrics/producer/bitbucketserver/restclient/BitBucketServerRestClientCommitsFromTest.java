package at.grisa.agilemetrics.producer.bitbucketserver.restclient;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerRestClient;
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
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitBucketServerRestClient.class, CredentialManager.class, ApplicationConfig.class})
@TestPropertySource("classpath:bitbucket-test.properties")
public class BitBucketServerRestClientCommitsFromTest {
    @Autowired
    private BitBucketServerRestClient client;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
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
        Date from = new Date();
        from.setTime(1518587500000L);
        commits = client.getCommits(projectKey, repositorySlug, from);
    }

    @Test
    public void countCommits() {
        assertEquals("3 commits from Wed Feb 14 2018 05:51:40", 3, commits.size());
    }
}