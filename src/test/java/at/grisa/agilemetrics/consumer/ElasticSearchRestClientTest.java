package at.grisa.agilemetrics.consumer;

import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchRestClient;
import at.grisa.agilemetrics.entity.Metric;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpResponse.response;

public class ElasticSearchRestClientTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private HttpRequest httpRequest;

    @Before
    public void mockESServer() throws URISyntaxException, IOException {
        String requestBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("elasticsearch/bulk-request.js").toURI())));
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("elasticsearch/bulk-response.js").toURI())));

        httpRequest = new HttpRequest();
        httpRequest.withMethod("POST")
                .withPath("/_bulk")
                .withBody(requestBody);

        mockServerClient.when(httpRequest)
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );
    }

    @Test
    public void bulkSaveMetrics() {
        ArrayList<Metric> metrics = new ArrayList<>();
        Map<String, String> meta = new HashMap<>();
        meta.put("meta", "data");
        meta.put("data", "meta");
        Set<String> tags = new HashSet<>();
        tags.add("some");
        tags.add("tags");
        metrics.add(new Metric(1.5, "metric1", meta, tags, OffsetDateTime.parse("2017-11-06T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime()));
        metrics.add(new Metric(2.5, "metric2", meta, tags, OffsetDateTime.parse("2017-11-06T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime()));
        metrics.add(new Metric(3.5, "metric3", meta, tags, OffsetDateTime.parse("2017-11-06T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime()));
        metrics.add(new Metric(4.5, "metric4", meta, tags, OffsetDateTime.parse("2017-11-06T11:00:46.000+0100", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toZonedDateTime()));

        ElasticSearchRestClient restClient = new ElasticSearchRestClient("http://localhost:" + mockServerRule.getPort());
        restClient.saveMetrics(metrics);

        mockServerClient.verify(httpRequest);
    }
}
