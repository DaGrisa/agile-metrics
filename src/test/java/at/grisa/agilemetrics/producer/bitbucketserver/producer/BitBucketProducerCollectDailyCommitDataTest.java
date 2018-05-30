package at.grisa.agilemetrics.producer.bitbucketserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerProducer;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerRestClient;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Commit;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Project;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Repository;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.User;
import at.grisa.agilemetrics.util.CredentialManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitBucketServerProducer.class, BitBucketProducerMockConfiguration.class, ApplicationConfig.class, CredentialManager.class})
public class BitBucketProducerCollectDailyCommitDataTest {
    @Autowired
    private BitBucketServerRestClient bitBucketServerRestClient;
    @Autowired
    private BitBucketServerProducer bitBucketServerProducer;
    @Autowired
    private MetricQueue metricQueue;

    private final String projectKey = "PRJ";
    private final String repositorySlug = "slug";
    private final String projectName = "project name";
    private final String repositoryName = "repo name";
    private final String author1Name = "author1 name";
    private final String author2Name = "author2 name";

    @Before
    public void createMocks() {
        Project project = new Project();
        project.setName(projectName);
        project.setKey(projectKey);
        when(bitBucketServerRestClient.getProjects()).thenReturn(Arrays.asList(project));

        Repository repository = new Repository();
        repository.setSlug(repositorySlug);
        repository.setName(repositoryName);
        when(bitBucketServerRestClient.getRepositories(projectKey)).thenReturn(Arrays.asList(repository));

        User author1 = new User();
        author1.setName(author1Name);
        User author2 = new User();
        author2.setName(author2Name);
        Commit commit1 = new Commit();
        commit1.setAuthor(author1);
        Commit commit2 = new Commit();
        commit2.setAuthor(author2);
        Commit commit3 = new Commit();
        commit3.setAuthor(author1);
        when(bitBucketServerRestClient.getCommits(eq(projectKey), eq(repositorySlug), any(Date.class))).thenReturn(Arrays.asList(commit1, commit2, commit3));
    }

    @Test
    public void collectDailyCommitDataTest() {
        bitBucketServerProducer.collectDailyCommitData();

        HashMap<String, String> commitsPerProjectMeta = new HashMap<>();
        commitsPerProjectMeta.put("project", projectName);
        Metric commitsPerProjectMetric = new Metric(3.0, "Commits per Project", commitsPerProjectMeta);

        HashMap<String, String> commitsPerAuthor1Meta = new HashMap<>();
        commitsPerAuthor1Meta.put("author", author1Name);
        Metric commitsPerAuthor1Metric = new Metric(2.0, "Commits per Author", commitsPerAuthor1Meta);

        HashMap<String, String> commitsPerAuthor2Meta = new HashMap<>();
        commitsPerAuthor2Meta.put("author", author2Name);
        Metric commitsPerAuthor2Metric = new Metric(1.0, "Commits per Author", commitsPerAuthor2Meta);

        HashMap<String, String> commitsPerRepositoryMeta = new HashMap<>();
        commitsPerRepositoryMeta.put("project", repositoryName);
        Metric commitsPerRepositoryMetric = new Metric(3.0, "Commits per Repository", commitsPerRepositoryMeta);

        verify(metricQueue).enqueueMetric(commitsPerProjectMetric);
        verify(metricQueue).enqueueMetric(commitsPerAuthor1Metric);
        verify(metricQueue).enqueueMetric(commitsPerAuthor2Metric);
        verify(metricQueue).enqueueMetric(commitsPerRepositoryMetric);
    }
}
