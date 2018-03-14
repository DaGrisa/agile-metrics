package at.grisa.agilemetrics.producer.bitbucket;

import at.grisa.agilemetrics.producer.bitbucket.restentities.Commit;
import at.grisa.agilemetrics.producer.bitbucket.restentities.PagedEntities;
import at.grisa.agilemetrics.producer.bitbucket.restentities.Project;
import at.grisa.agilemetrics.producer.bitbucket.restentities.Repository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;

public class BitBucketRestClient {
    private final String hostUrl;
    private final String username;
    private final String password;

    public BitBucketRestClient(String hostUrl, String user, String password) {
        this.hostUrl = hostUrl;
        this.username = user;
        this.password = password;
    }

    public Project[] getProjects() {
        String projectsPath = "/rest/api/1.0/projects";
        String requestUrl = hostUrl + projectsPath;
        return getPagedEntities(Project.class, new ParameterizedTypeReference<PagedEntities<Project>>() {
        }, requestUrl);
    }

    public Repository[] getRepos(String projectKey) {
        String reposPath = "/rest/api/1.0/projects/{projectKey}/repos";
        String requestUrl = hostUrl + reposPath.replace("{projectKey}", projectKey);
        return getPagedEntities(Repository.class, new ParameterizedTypeReference<PagedEntities<Repository>>() {
        }, requestUrl);
    }

    public Commit[] getCommits(String projectKey, String repoSlug) {
        String commitsPath = "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits";
        String requestUrl = hostUrl + commitsPath.replace("{projectKey}", projectKey).replace("{repositorySlug}", repoSlug);
        return getPagedEntities(Commit.class, new ParameterizedTypeReference<PagedEntities<Commit>>() {
        }, requestUrl);
    }

    private <T> T[] getPagedEntities(Class<T> clazz, ParameterizedTypeReference<PagedEntities<T>> responseType, String restUrl) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        @SuppressWarnings("unchecked")
        T[] entities = (T[]) Array.newInstance(clazz, 0);
        PagedEntities<T> response;
        Long startElement = 0L;

        do {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl)
                    .queryParam("start", startElement);

            response = restTemplate.exchange(builder.build().encode().toUri(),
                    HttpMethod.GET,
                    null,
                    responseType
            ).getBody();

            if (response != null) {
                entities = mergeArrays(entities, response.getValues(), clazz);
                startElement = response.getNextPageStart();
            } else {
                break;
            }
        } while (!response.getIsLastPage());

        return entities;
    }

    private <T> T[] mergeArrays(T[] entities, T[] responseEntities, Class<T> clazz) {
        int totalLength = entities.length + responseEntities.length;

        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz, totalLength);

        int index = 0;
        for (T entity : entities) {
            result[index++] = entity;
        }
        for (T entity : responseEntities) {
            result[index++] = entity;
        }

        return result;
    }
}
