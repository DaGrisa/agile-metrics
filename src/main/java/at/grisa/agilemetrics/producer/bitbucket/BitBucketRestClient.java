package at.grisa.agilemetrics.producer.bitbucket;

import at.grisa.agilemetrics.producer.bitbucket.restentities.PagedEntities;
import at.grisa.agilemetrics.producer.bitbucket.restentities.Project;
import at.grisa.agilemetrics.producer.bitbucket.restentities.Repo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

public class BitBucketRestClient {
    private String hostUrl;
    private String username;
    private String password;

    private final String PROJECTS_URL = "/rest/api/1.0/projects";
    private final String REPOS_URL = "/rest/api/1.0/projects/{projectKey}/repos";

    public BitBucketRestClient(String hostUrl, String user, String password) {
        this.hostUrl = hostUrl;
        this.username = user;
        this.password = password;
    }

    public Project[] getProjects() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        Project[] projects = {};
        PagedEntities<Project> projectsResponse;
        Long startElement = 0L;

        do {
            projectsResponse = restTemplate.exchange(hostUrl + PROJECTS_URL + "?start=" + startElement,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PagedEntities<Project>>() {
                    }).getBody();

            // merge arrays and set next start element
            projects = Stream.of(projects, projectsResponse.getValues()).flatMap(Stream::of).toArray(Project[]::new);
            startElement = projectsResponse.getNextPageStart();
        } while (!projectsResponse.getIsLastPage());

        return projects;
    }

    public Repo[] getRepos(String projectKey) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        PagedEntities<Repo> repos = restTemplate.exchange(hostUrl + REPOS_URL.replace("{projectKey}", projectKey),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedEntities<Repo>>() {
                }).getBody();

        // TODO add more repos, if exists

        return repos.getValues();
    }
}
