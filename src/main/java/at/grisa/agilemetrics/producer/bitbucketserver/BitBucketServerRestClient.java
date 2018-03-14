package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Commit;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.PagedEntities;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Project;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Repository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class BitBucketServerRestClient {
    private final String hostUrl;
    private final String username;
    private final String password;

    public BitBucketServerRestClient(String hostUrl, String user, String password) {
        this.hostUrl = hostUrl;
        this.username = user;
        this.password = password;
    }

    public Collection<Project> getProjects() {
        String projectsPath = "/rest/api/1.0/projects";
        String requestUrl = hostUrl + projectsPath;
        return Arrays.asList(getAllEntities(Project.class, new ParameterizedTypeReference<PagedEntities<Project>>() {
        }, requestUrl));
    }

    public Collection<Repository> getRepositories(String projectKey) {
        String reposPath = "/rest/api/1.0/projects/{projectKey}/repos";
        String requestUrl = hostUrl + reposPath.replace("{projectKey}", projectKey);
        return Arrays.asList(getAllEntities(Repository.class, new ParameterizedTypeReference<PagedEntities<Repository>>() {
        }, requestUrl));
    }

    public Collection<Commit> getCommits(String projectKey, String repoSlug) {
        String commitsPath = "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits";
        String requestUrl = hostUrl + commitsPath.replace("{projectKey}", projectKey).replace("{repositorySlug}", repoSlug);
        return Arrays.asList(getAllEntities(Commit.class, new ParameterizedTypeReference<PagedEntities<Commit>>() {
        }, requestUrl, new QueryParam("withCounts", true)));
    }

    public Collection<Commit> getCommits(String projectKey, String repoSlug, Date from) {
        String commitsPath = "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits";
        String requestUrl = hostUrl + commitsPath.replace("{projectKey}", projectKey).replace("{repositorySlug}", repoSlug);

        LinkedList<Commit> commits = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean commitsToLoad = true;

        while (commitsToLoad) {
            QueryParam startElement = new QueryParam("start", startElementIndex);
            PagedEntities<Commit> pagedCommits = getPagedEntities(Commit.class, new ParameterizedTypeReference<PagedEntities<Commit>>() {
            }, requestUrl, startElement);

            for (Commit commit : pagedCommits.getValues()) {
                if (commit.getAuthorTimestamp().after(from)) {
                    commits.add(commit);
                } else {
                    commitsToLoad = false;
                }
            }

            startElementIndex = pagedCommits.getNextPageStart();
        }

        return commits;
    }

    private <T> PagedEntities<T> getPagedEntities(Class<T> clazz, ParameterizedTypeReference<PagedEntities<T>> responseType, String restUrl,
                                                  QueryParam... queryParams) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl);

        for (QueryParam queryParam : queryParams) {
            builder.queryParam(queryParam.name, queryParam.value);
        }

        PagedEntities<T> response = restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                null,
                responseType
        ).getBody();

        return response;
    }

    private <T> T[] getAllEntities(Class<T> clazz, ParameterizedTypeReference<PagedEntities<T>> responseType, String restUrl,
                                   QueryParam... queryParams) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        @SuppressWarnings("unchecked")
        T[] entities = (T[]) Array.newInstance(clazz, 0);
        PagedEntities<T> response;
        Integer startElement = 0;

        do {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl)
                    .queryParam("start", startElement);

            for (QueryParam queryParam : queryParams) {
                builder.queryParam(queryParam.name, queryParam.value);
            }

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

    class QueryParam {
        final String name;
        final Object value;

        public QueryParam(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}
