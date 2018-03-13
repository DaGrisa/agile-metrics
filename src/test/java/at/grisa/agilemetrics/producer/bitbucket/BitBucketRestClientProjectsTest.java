package at.grisa.agilemetrics.producer.bitbucket;

import at.grisa.agilemetrics.mockserver.MockServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class BitBucketRestClientProjectsTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;

    @BeforeClass
    public static void startMockServer() {
        MockServer.getInstance().startClass(BitBucketRestClientProjectsTest.class);


    }

    @Test
    public void testGetAllProjects() {
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody("{\n" +
                                        "    \"size\": 1,\n" +
                                        "    \"limit\": 25,\n" +
                                        "    \"isLastPage\": true,\n" +
                                        "    \"values\": [\n" +
                                        "        {\n" +
                                        "            \"key\": \"PRJ\",\n" +
                                        "            \"id\": 1,\n" +
                                        "            \"name\": \"My Cool Project\",\n" +
                                        "            \"description\": \"The description for my cool project.\",\n" +
                                        "            \"public\": true,\n" +
                                        "            \"type\": \"NORMAL\",\n" +
                                        "            \"links\": {\n" +
                                        "                \"self\": [\n" +
                                        "                    {\n" +
                                        "                        \"href\": \"http://link/to/project\"\n" +
                                        "                    }\n" +
                                        "                ]\n" +
                                        "            }\n" +
                                        "        }\n" +
                                        "    ],\n" +
                                        "    \"start\": 0\n" +
                                        "}")
                );

        BitBucketRestClient client = new BitBucketRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        assertEquals("1 project in total", client.getProjects().length, 1);
    }
}