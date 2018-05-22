package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.producer.atlassian.rest.RestClientAtlassian;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.*;
import at.grisa.agilemetrics.util.CredentialManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@Component
public class BitBucketServerRestClient {
    private static final Logger log = LogManager.getLogger(BitBucketServerRestClient.class);

    private final RestClientAtlassian restClientAtlassian;

    private static final String PROJECTKEY_PLACEHOLDER = "{projectKey}";

    private static final String CHECK_PATH = "/rest/api/1.0/application-properties";
    private static final String PROJECTS_PATH = "/rest/api/1.0/projects";
    private static final String REPOS_PATH = "/rest/api/1.0/projects/{projectKey}/repos";
    private static final String COMMITS_PATH = "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits";

    public BitBucketServerRestClient(CredentialManager credentialManager) {
        String hostUrl = credentialManager.getBitbucketserverBaseUrl();
        String user = credentialManager.getBitbucketserverUsername();
        String password = credentialManager.getBitbucketserverPassword();

        this.restClientAtlassian = new RestClientAtlassian(hostUrl, user, password);

        // set proxy if configured
        if (credentialManager.isProxyAuthActive()) {
            restClientAtlassian.setHttpProxyAuth(credentialManager.getHttpProxyHost(), credentialManager.getHttpProxyPort(), credentialManager.getHttpProxyUser(), credentialManager.getHttpProxyPassword());
        } else if (credentialManager.isProxyActive()) {
            restClientAtlassian.setHttpProxy(credentialManager.getHttpProxyHost(), credentialManager.getHttpProxyPort());
        }
    }

    public boolean checkConnection() {
        ApplicationProperties applicationProperties = restClientAtlassian.getEntity(ApplicationProperties.class, CHECK_PATH);
        log.info("BitBucket connection check returns " + applicationProperties);
        return applicationProperties != null && !applicationProperties.isEmpty();
    }

    public Collection<Project> getProjects() {
        return Arrays.asList(restClientAtlassian.getAllEntities(Project.class, new ParameterizedTypeReference<PagedEntities<Project>>() {
        }, PROJECTS_PATH));
    }

    public Collection<Repository> getRepositories(String projectKey) {

        String requestPath = REPOS_PATH.replace(PROJECTKEY_PLACEHOLDER, projectKey);
        return Arrays.asList(restClientAtlassian.getAllEntities(Repository.class, new ParameterizedTypeReference<PagedEntities<Repository>>() {
        }, requestPath));
    }

    public Collection<Commit> getCommits(String projectKey, String repoSlug) {
        String requestPath = COMMITS_PATH.replace(PROJECTKEY_PLACEHOLDER, projectKey).replace("{repositorySlug}", repoSlug);
        try {
            return Arrays.asList(restClientAtlassian.getAllEntities(Commit.class, new ParameterizedTypeReference<PagedEntities<Commit>>() {
            }, requestPath, new QueryParam("withCounts", true)));
        } catch (RuntimeException e) {
            if (e.getCause().getMessage().contains("404")) {
                log.info("No commits so far in project " + projectKey + " of repository " + repoSlug);
                return null;
            } else {
                throw e;
            }
        }

    }

    public Collection<Commit> getCommits(String projectKey, String repoSlug, Date from) {

        String requestPath = COMMITS_PATH.replace(PROJECTKEY_PLACEHOLDER, projectKey).replace("{repositorySlug}", repoSlug);

        LinkedList<Commit> commits = new LinkedList<>();
        Integer startElementIndex = 0;
        Boolean commitsToLoad = true;

        while (commitsToLoad) {
            QueryParam startElement = new QueryParam("start", startElementIndex);

            PagedEntities<Commit> pagedCommits = null;
            try {
                pagedCommits = restClientAtlassian.getPagedEntities(new ParameterizedTypeReference<PagedEntities<Commit>>() {
                }, requestPath, startElement);
            } catch (RuntimeException e) {
                if (e.getCause().getMessage().contains("404")) {
                    log.info("No commits so far in project " + projectKey + " of repository " + repoSlug);
                } else {
                    throw e;
                }
            }

            if (pagedCommits != null) {
                for (Commit commit : pagedCommits.getValues()) {
                    if (commit.getAuthorTimestamp().after(from)) {
                        commits.add(commit);
                    } else {
                        commitsToLoad = false;
                    }
                }

                startElementIndex = pagedCommits.getNextPageStart();
            } else {
                break;
            }
        }

        return commits;
    }
}
