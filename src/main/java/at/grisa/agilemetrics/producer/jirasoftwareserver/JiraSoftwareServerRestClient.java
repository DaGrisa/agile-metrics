package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.producer.atlassian.rest.RestClientAtlassian;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.*;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.RapidView;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.RapidViews;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.SprintReport;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.Sprints;
import at.grisa.agilemetrics.util.CredentialManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JiraSoftwareServerRestClient {
    public static final String QUERYPARAM_START = "start";
    public static final String PLACEHOLDER_BOARDID = "{boardId}";
    public static final String PLACEHOLDER_SPRINTID = "{sprintId}";

    private final RestClientAtlassian restClientAtlassian;
    public static final String PATH_BOARDS = "/rest/agile/1.0/board";
    public static final String PATH_BOARDCONFIG = "/rest/agile/1.0/board/{boardId}/configuration";
    public static final String PATH_BOARDFILTER = "/rest/api/2/filter/{filterId}";
    public static final String PATH_ISSUE = "/rest/api/2/issue/{issueId}";
    public static final String PATH_SEARCH = "/rest/api/2/search";
    public static final String PATH_BOARDSPRINTS = "/rest/agile/1.0/board/{boardId}/sprint";
    public static final String PATH_SPRINTISSUES = "/rest/agile/1.0/board/{boardId}/sprint/{sprintId}/issue";
    public static final String PATH_RAPIDVIEWS = "/rest/greenhopper/1.0/rapidview";
    public static final String PATH_SPRINTQUERY = "/rest/greenhopper/1.0/sprintquery/{rapidViewId}";
    public static final String PATH_SPRINTREPORT = "/rest/greenhopper/1.0/rapid/charts/sprintreport";
    public static final String PATH_VELOCITYREPORT = "/rest/greenhopper/1.0/rapid/charts/velocity";

    public JiraSoftwareServerRestClient(CredentialManager credentialManager) {
        String hostUrl = credentialManager.getJirasoftwareBaseUrl();
        String user = credentialManager.getJirasoftwareUsername();
        String password = credentialManager.getJirasoftwarePassword();

        this.restClientAtlassian = new RestClientAtlassian(hostUrl, user, password);
    }

    public Collection<Board> getScrumBoards() {
        LinkedList<Board> boards = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean boardsToLoad = true;

        while (boardsToLoad) {
            QueryParam startElement = new QueryParam(QUERYPARAM_START, startElementIndex);
            PagedEntities<Board> pagedBoards = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Board>>() {
            }, PATH_BOARDS, startElement);

            for (Board board : pagedBoards.getValues()) {
                if (board.getType().equalsIgnoreCase("scrum")) {
                    boards.add(board);
                }
            }

            startElementIndex = pagedBoards.getStartAt() + pagedBoards.getMaxResults();
            boardsToLoad = pagedBoards.getLast() != null && !pagedBoards.getLast();
        }

        return boards;
    }

    public String getScrumBoardJQLFilter(Long boardId) {
        String boardConfigRequestPath = PATH_BOARDCONFIG.replace(PLACEHOLDER_BOARDID, boardId.toString());
        BoardConfiguration boardConfiguration = restClientAtlassian.getEntity(BoardConfiguration.class, boardConfigRequestPath);

        String boardFilterRequestPath = PATH_BOARDFILTER.replace("{filterId}", boardConfiguration.getFilter().getId().toString());
        Filter boardFilter = restClientAtlassian.getEntity(Filter.class, boardFilterRequestPath);

        return boardFilter.getJql();
    }

    public Issue getIssue(Long issueId, QueryParam... queryParams) {
        String issueRequestPath = PATH_ISSUE.replace("{issueId}", issueId.toString());
        Issue issue = restClientAtlassian.getEntity(Issue.class, issueRequestPath, queryParams);

        return issue;
    }

    public Collection<Issue> getIssuesByJQL(String jql) {
        QueryParam jqlQueryParam = new QueryParam("jql", jql);

        LinkedList<Issue> issues = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean issuesToLoad = true;

        while (issuesToLoad) {
            QueryParam startElementIndexQueryParam = new QueryParam(QUERYPARAM_START, startElementIndex);
            PagedEntities<Issue> pagedIssues = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Issue>>() {
            }, PATH_SEARCH, jqlQueryParam, startElementIndexQueryParam);

            issues.addAll(Arrays.asList(pagedIssues.getIssues()));

            startElementIndex = pagedIssues.getStartAt() + pagedIssues.getMaxResults();
            issuesToLoad = pagedIssues.getLast() != null && !pagedIssues.getLast();
        }

        return issues;
    }

    public Sprint getActiveSprint(Long board) {
        String requestPath = PATH_BOARDSPRINTS.replace(PLACEHOLDER_BOARDID, board.toString());
        Integer startElementIndex = 0;
        Boolean sprintsToLoad = true;

        while (sprintsToLoad) {
            QueryParam startElement = new QueryParam(QUERYPARAM_START, startElementIndex);
            PagedEntities<Sprint> pagedSprints = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Sprint>>() {
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
        String requestPath = PATH_SPRINTISSUES.replace(PLACEHOLDER_BOARDID, boardId.toString()).replace(PLACEHOLDER_SPRINTID, sprintId.toString());

        PagedEntities<Issue> pagedIssues = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Issue>>() {
        }, requestPath);

        return pagedIssues.getTotal();
    }

    public Collection<Issue> getSprintIssues(Long boardId, Long sprintId, QueryParam additionalParameter) {
        String requestPath = JiraSoftwareServerRestClient.PATH_SPRINTISSUES.replace(PLACEHOLDER_BOARDID, boardId.toString()).replace(PLACEHOLDER_SPRINTID, sprintId.toString());
        LinkedList<Issue> issues = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean issuesToLoad = true;

        while (issuesToLoad) {
            QueryParam startElement = new QueryParam(QUERYPARAM_START, startElementIndex);

            PagedEntities<Issue> pagedIssues = null;
            if (additionalParameter != null) {
                pagedIssues = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Issue>>() {
                }, requestPath, startElement, additionalParameter);
            } else {
                pagedIssues = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Issue>>() {
                }, requestPath, startElement);
            }

            issues.addAll(Arrays.asList(pagedIssues.getIssues()));

            startElementIndex = pagedIssues.getStartAt() + pagedIssues.getMaxResults();
            issuesToLoad = pagedIssues.getLast() != null && !pagedIssues.getLast();
        }

        return issues;
    }

    public Collection<Issue> getSprintIssues(Long boardId, Long sprintId) {
        return this.getSprintIssues(boardId, sprintId, null);
    }

    public Collection<Issue> getSprintIssuesStatus(Long boardId, Long sprintId) {
        return this.getSprintIssues(boardId, sprintId, new QueryParam("fields", "status"));
    }

    public Collection<RapidView> getRapidViewsGreenhopper() {
        RapidViews rapidViewsResponse = restClientAtlassian.getEntity(RapidViews.class, PATH_RAPIDVIEWS);

        return Arrays.stream(rapidViewsResponse.getViews())
                .filter(rapidView -> rapidView.getSprintSupportEnabled())
                .collect(Collectors.toList());
    }

    public at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.Sprint getActiveSprintGreenhopper(Long rapidviewId) {
        String sprintsRequestPath = JiraSoftwareServerRestClient.PATH_SPRINTQUERY.replace("{rapidViewId}", rapidviewId.toString());

        Sprints sprintsResponse = restClientAtlassian.getEntity(Sprints.class, sprintsRequestPath);
        List<at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.Sprint> sprints = Arrays.stream(sprintsResponse.getSprints())
                .filter(sprint -> sprint.getState().equalsIgnoreCase("active"))
                .collect(Collectors.toList());

        return !sprints.isEmpty() ? sprints.get(0) : null;
    }

    public SprintReport getSprintReportGreenhopper(Long rapidviewId, Long sprintId) {
        QueryParam rapidViewIdQueryParam = new QueryParam("rapidViewId", rapidviewId);
        QueryParam sprintIdQueryParam = new QueryParam("sprintId", sprintId);

        return restClientAtlassian.getEntity(SprintReport.class, PATH_SPRINTREPORT, rapidViewIdQueryParam, sprintIdQueryParam);
    }

    public VelocityReport getVelocityReportGreenhopper(Long rapidviewId) {
        QueryParam rapidViewIdQueryParam = new QueryParam("rapidViewId", rapidviewId);

        return restClientAtlassian.getEntity(VelocityReport.class, PATH_VELOCITYREPORT, rapidViewIdQueryParam);
    }
}
