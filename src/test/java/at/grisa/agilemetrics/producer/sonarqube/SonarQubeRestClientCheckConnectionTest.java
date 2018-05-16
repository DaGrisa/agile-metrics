package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.ApplicationConfig;
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

import static org.junit.Assert.assertTrue;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SonarQubeRestClient.class, CredentialManager.class, ApplicationConfig.class})
@TestPropertySource("classpath:sonarqube-test.properties")
public class SonarQubeRestClientCheckConnectionTest {
    @Autowired
    private SonarQubeRestClient client;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
    private MockServerClient mockServerClient;

    @Before
    public void loginMockServer() {
        mockServerClient.when(
                request()
                        .withMethod("POST")
                        .withPath("/api/authenticaton/login")
                        .withQueryStringParameter("login", "user")
                        .withQueryStringParameter("password", "password")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                );
    }

    @Test
    public void checkConnection() {
        assertTrue("check connection", client.checkConnection());
    }
}
