package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.cron.TimeSpan;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Commit;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Project;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Repository;
import at.grisa.agilemetrics.util.CredentialManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class BitBucketServerProducer implements IProducer {
    @Autowired
    private CredentialManager credentialManager;

    private BitBucketServerRestClient bitBucketServerRestClient;

    public BitBucketServerProducer() {
        bitBucketServerRestClient = new BitBucketServerRestClient(credentialManager.getBitbucketserverUsername(),
                credentialManager.getBitbucketserverPassword(),
                credentialManager.getBitbucketserverBaseUrl());
    }

    @Override
    public void produce(MetricQueue metricQueue, TimeSpan timespan) {
        if (timespan.equals(TimeSpan.DAILY)) {
            collectDailyCommitData(metricQueue);
        }
    }

    private void collectDailyCommitData(MetricQueue metricQueue) {
        Date created = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(created);
        cal.add(Calendar.DATE, -1);
        Date from = cal.getTime();

        HashMap<String, Integer> commitsPerProject = new HashMap<>();
        HashMap<String, Integer> commitsPerAuthor = new HashMap<>();
        HashMap<String, Integer> commitsPerRepository = new HashMap<>();

        // get all commits per project / repository / author
        Collection<Project> projects = bitBucketServerRestClient.getProjects();
        for (Project project : projects) {
            Collection<Repository> repositories = bitBucketServerRestClient.getRepositories(project.getKey());
            for (Repository repository : repositories) {
                Collection<Commit> commits = bitBucketServerRestClient.getCommits(project.getKey(), repository.getSlug(), from);
                for (Commit commit : commits) {
                    commitsPerProject.put(project.getName(), commitsPerProject.get(project.getName()) + 1);
                    commitsPerAuthor.put(commit.getAuthor().getName(), commitsPerAuthor.get(commit.getAuthor().getName()) + 1);
                    commitsPerRepository.put(repository.getName(), commitsPerRepository.get(repository.getName()) + 1);
                }
            }
        }

        // enqueue project commits metric
        for (Map.Entry<String, Integer> commitsPerProjectValue : commitsPerProject.entrySet()) {
            HashMap<String, String> commitsPerProjectMeta = new HashMap<>();
            commitsPerProjectMeta.put("project", commitsPerProjectValue.getKey());
            Metric commitsPerProjectMetric = new Metric(commitsPerProjectValue.getValue().doubleValue(), "Commits per Project", commitsPerProjectMeta);
            metricQueue.enqueueMetric(commitsPerProjectMetric);
        }

        // enqueue author commits metric
        for (Map.Entry<String, Integer> commitsPerAuthorValue : commitsPerAuthor.entrySet()) {
            HashMap<String, String> commitsPerAuthorMeta = new HashMap<>();
            commitsPerAuthorMeta.put("author", commitsPerAuthorValue.getKey());
            Metric commitsPerAuthorMetric = new Metric(commitsPerAuthorValue.getValue().doubleValue(), "Commits per Author", commitsPerAuthorMeta);
            metricQueue.enqueueMetric(commitsPerAuthorMetric);
        }

        // enqueue repository commits metric
        for (Map.Entry<String, Integer> commitsPerRepositoryValue : commitsPerRepository.entrySet()) {
            HashMap<String, String> commitsPerRepositoryMeta = new HashMap<>();
            commitsPerRepositoryMeta.put("project", commitsPerRepositoryValue.getKey());
            Metric commitsPerRepositoryMetric = new Metric(commitsPerRepositoryValue.getValue().doubleValue(), "Commits per Repository", commitsPerRepositoryMeta);
            metricQueue.enqueueMetric(commitsPerRepositoryMetric);
        }

    }

    private void collectDailyChangedLines(MetricQueue metricQueue) {

    }

    private void collectDailyNewPullRequests(MetricQueue metricQueue) {

    }

    private void collectDailyActivePullRequests(MetricQueue metricQueue) {

    }
}
