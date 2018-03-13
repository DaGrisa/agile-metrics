package at.grisa.agilemetrics.producer.bitbucket;

import at.grisa.agilemetrics.producer.bitbucket.restentities.PagedEntities;
import at.grisa.agilemetrics.producer.bitbucket.restentities.Project;
import at.grisa.agilemetrics.producer.bitbucket.restentities.Repo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

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

        PagedEntities<Project> projects = restTemplate.exchange(hostUrl + PROJECTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedEntities<Project>>() {
                }).getBody();

        // TODO add more projects, if exists

        return projects.getValues();
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
