package at.grisa.agilemetrics.producer.jirasoftwareserver.restclient;

import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Board;
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

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class JiraSoftwareServerRestClientScrumBoardsTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;
    private Collection<Board> boards;

    @Before
    public void loadBoardsFromMockServer() throws URISyntaxException, IOException {
        String responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("jirasoftware/boards.js").toURI())));

        mockServerClient.when(
                request()
                        .withMethod("GET")
                        .withPath("/rest/agile/1.0/board")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(header("Content-Type", "application/json; charset=utf-8"))
                                .withBody(responseBody)
                );

        JiraSoftwareServerRestClient client = new JiraSoftwareServerRestClient("http://localhost:" + mockServerRule.getPort(), "user", "password");
        boards = client.getScrumBoards();
    }

    @Test
    public void countBoards() {
        assertEquals("2 boards in total", 2, boards.size());
    }

    @Test
    public void checkData() {
        Board board = boards.iterator().next();
        assertEquals("check board id", new Long(84), board.getId());
        assertEquals("check board name", "scrum board", board.getName());
        assertEquals("check board type", "scrum", board.getType());
    }
}
