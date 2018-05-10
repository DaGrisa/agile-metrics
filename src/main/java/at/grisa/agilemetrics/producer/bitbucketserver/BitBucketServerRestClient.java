package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.producer.atlassian.rest.RestClientAtlassian;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Commit;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.PagedEntities;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Project;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Repository;
import at.grisa.agilemetrics.util.CredentialManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@Component
public class BitBucketServerRestClient {
    private final RestClientAtlassian restClientAtlassian;

    public BitBucketServerRestClient(CredentialManager credentialManager) {
        String hostUrl = credentialManager.getBitbucketserverBaseUrl();
        String user = credentialManager.getBitbucketserverUsername();
        String password = credentialManager.getBitbucketserverPassword();

        this.restClientAtlassian = new RestClientAtlassian(hostUrl, user, password);
    }

    public Collection<Project> getProjects() {
        String projectsPath = "/rest/api/1.0/projects";
        return Arrays.asList(restClientAtlassian.getAllEntities(Project.class, new ParameterizedTypeReference<PagedEntities<Project>>() {
        }, projectsPath));
    }

    public Collection<Repository> getRepositories(String projectKey) {
        String reposPath = "/rest/api/1.0/projects/{projectKey}/repos";
        String requestPath = reposPath.replace("{projectKey}", projectKey);
        return Arrays.asList(restClientAtlassian.getAllEntities(Repository.class, new ParameterizedTypeReference<PagedEntities<Repository>>() {
        }, requestPath));
    }

    public Collection<Commit> getCommits(String projectKey, String repoSlug) {
        String commitsPath = "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits";
        String requestPath = commitsPath.replace("{projectKey}", projectKey).replace("{repositorySlug}", repoSlug);
        return Arrays.asList(restClientAtlassian.getAllEntities(Commit.class, new ParameterizedTypeReference<PagedEntities<Commit>>() {
        }, requestPath, new QueryParam("withCounts", true)));
    }

    public Collection<Commit> getCommits(String projectKey, String repoSlug, Date from) {
        String commitsPath = "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits";
        String requestPath = commitsPath.replace("{projectKey}", projectKey).replace("{repositorySlug}", repoSlug);

        LinkedList<Commit> commits = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean commitsToLoad = true;

        while (commitsToLoad) {
            QueryParam startElement = new QueryParam("start", startElementIndex);
            PagedEntities<Commit> pagedCommits = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Commit>>() {
            }, requestPath, startElement);

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
}
