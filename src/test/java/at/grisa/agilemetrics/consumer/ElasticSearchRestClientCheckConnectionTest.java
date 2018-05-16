package at.grisa.agilemetrics.consumer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchRestClient;
import at.grisa.agilemetrics.cron.MetricErrorHandler;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ElasticSearchRestClient.class, CredentialManager.class, PropertyManager.class, MetricErrorHandler.class, ApplicationConfig.class})
@TestPropertySource("classpath:elasticsearch-test.properties")
public class ElasticSearchRestClientCheckConnectionTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
    private MockServerClient mockServerClient;
    private HttpRequest httpRequest;

    @Autowired
    private ElasticSearchRestClient restClient;

    @Before
    public void mockESServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("elasticsearch/check-connection.js").toURI())));

        httpRequest = new HttpRequest();
        httpRequest.withMethod("GET")
                .withPath("/");

        mockServerClient.when(httpRequest)
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );
    }

    @Test
    public void checkConnection() {
        assertTrue("check connection", restClient.checkConnection());
    }
}
