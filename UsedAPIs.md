# APIs Used

## BitBucket Server

API Documentation: https://docs.atlassian.com/bitbucket-server/rest/5.9.0/bitbucket-rest.html

### Projects and Repositories

Every repository must be withing a project.

Projects: [GET http://example.com/rest/projects](https://docs.atlassian.com/bitbucket-server/rest/5.9.0/bitbucket-rest.html#idm45328869225824)
Repositories: [GET http://example.com/rest/projects/{projectKey}/repos](https://docs.atlassian.com/bitbucket-server/rest/5.9.0/bitbucket-rest.html#idm45328868723456)

### Commits

[GET http://example.com/rest/projects/{projectKey}/repos/{repositorySlug}/commits](https://docs.atlassian.com/bitbucket-server/rest/5.9.0/bitbucket-rest.html#idm45328867726144)

### PRs