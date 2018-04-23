package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.producer.atlassian.rest.RestClient;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Issue;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.PagedEntities;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Sprint;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class JiraSoftwareServerRestClient {
    private final RestClient restClient;

    public JiraSoftwareServerRestClient(String hostUrl, String user, String password) {
        this.restClient = new RestClient(hostUrl, user, password);
    }

    public Collection<Board> getAllScrumBoards() {
        String boardsPath = "/rest/agile/1.0/board";
        LinkedList<Board> boards = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean boardsToLoad = true;

        while (boardsToLoad) {
            QueryParam startElement = new QueryParam("start", startElementIndex);
            PagedEntities<Board> pagedBoards = restClient.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Board>>() {
            }, boardsPath, startElement);

            for (Board board : pagedBoards.getValues()) {
                if (board.getType().toLowerCase().equals("scrum")) {
                    boards.add(board);
                }
            }

            startElementIndex = pagedBoards.getStartAt() + pagedBoards.getMaxResults();
            boardsToLoad = pagedBoards.getLast() != null && !pagedBoards.getLast();
        }

        return boards;
    }

    public Sprint getActiveSprint(Long board) {
        String sprintsPath = "/rest/agile/1.0/board/{boardId}/sprint";
        String requestPath = sprintsPath.replace("{boardId}", board.toString());
        Integer startElementIndex = 0;
        Boolean sprintsToLoad = true;

        while (sprintsToLoad) {
            QueryParam startElement = new QueryParam("start", startElementIndex);
            PagedEntities<Sprint> pagedSprints = restClient.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Sprint>>() {
            }, requestPath, startElement);

            for (Sprint sprint : pagedSprints.getValues()) {
                if (sprint.getState().equals("active")) {
                    return sprint;
                }
            }

            startElementIndex = pagedSprints.getStartAt() + pagedSprints.getMaxResults();
            sprintsToLoad = pagedSprints.getLast() != null && !pagedSprints.getLast();
        }

        return null;
    }

    public Integer getSprintIssuesCount(Long boardId, Long sprintId) {
        String issuesPath = "/rest/agile/1.0/board/{boardId}/sprint/{sprintId}/issue";
        String requestPath = issuesPath.replace("{boardId}", boardId.toString()).replace("{sprintId}", sprintId.toString());
        LinkedList<Issue> issues = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean issuesToLoad = true;

        PagedEntities<Issue> pagedIssues = restClient.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Issue>>() {
        }, requestPath);

        return pagedIssues.getTotal();
    }

    public Collection<Issue> getSprintIssues(Long boardId, Long sprintId) {
        String issuesPath = "/rest/agile/1.0/board/{boardId}/sprint/{sprintId}/issue";
        String requestPath = issuesPath.replace("{boardId}", boardId.toString()).replace("{sprintId}", sprintId.toString());
        LinkedList<Issue> issues = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean issuesToLoad = true;

        while (issuesToLoad) {
            QueryParam startElement = new QueryParam("start", startElementIndex);
            PagedEntities<Issue> pagedIssues = restClient.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Issue>>() {
            }, requestPath, startElement);

            issues.addAll(Arrays.asList(pagedIssues.getIssues()));

            startElementIndex = pagedIssues.getStartAt() + pagedIssues.getMaxResults();
            issuesToLoad = pagedIssues.getLast() != null && !pagedIssues.getLast();
        }

        return issues;
    }
}
