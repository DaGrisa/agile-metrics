package at.grisa.agilemetrics.producer.bitbucket;

import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class BitBucketRestClientReposTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;

    @Test
    public void testGetAllRepos() {
        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/api/1.0/projects/PRJ/repos")
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
                                        "            \"slug\": \"my-repo\",\n" +
                                        "            \"id\": 1,\n" +
                                        "            \"name\": \"My repo\",\n" +
                                        "            \"scmId\": \"git\",\n" +
                                        "            \"state\": \"AVAILABLE\",\n" +
                                        "            \"statusMessage\": \"Available\",\n" +
                                        "            \"forkable\": true,\n" +
                                        "            \"project\": {\n" +
                                        "                \"key\": \"PRJ\",\n" +
                                        "                \"id\": 1,\n" +
                                        "                \"name\": \"My Cool Project\",\n" +
                                        "                \"description\": \"The description for my cool project.\",\n" +
                                        "                \"public\": true,\n" +
                                        "                \"type\": \"NORMAL\",\n" +
                                        "                \"links\": {\n" +
                                        "                    \"self\": [\n" +
                                        "                        {\n" +
                                        "                            \"href\": \"http://link/to/project\"\n" +
                                        "                        }\n" +
                                        "                    ]\n" +
                                        "                }\n" +
                                        "            },\n" +
                                        "            \"public\": true,\n" +
                                        "            \"links\": {\n" +
                                        "                \"clone\": [\n" +
                                        "                    {\n" +
                                        "                        \"href\": \"ssh://git@<baseURL>/PRJ/my-repo.git\",\n" +
                                        "                        \"name\": \"ssh\"\n" +
                                        "                    },\n" +
                                        "                    {\n" +
                                        "                        \"href\": \"https://<baseURL>/scm/PRJ/my-repo.git\",\n" +
                                        "                        \"name\": \"http\"\n" +
                                        "                    }\n" +
                                        "                ],\n" +
                                        "                \"self\": [\n" +
                                        "                    {\n" +
                                        "                        \"href\": \"http://link/to/repository\"\n" +
                                        "                    }\n" +
                                        "                ]\n" +
                                        "            }\n" +
                                        "        }\n" +
                                        "    ],\n" +
                                        "    \"start\": 0\n" +
                                        "}")
                );

        String projectKey = "PRJ";
        BitBucketRestClient client = new BitBucketRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        assertEquals("1 project in total", client.getRepos(projectKey).length, 1);
    }
}