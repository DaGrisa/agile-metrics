package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Commit;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Project;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.Repository;
import at.grisa.agilemetrics.util.CredentialManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Lazy
public class BitBucketServerProducer implements IProducer {
    private static final Logger log = LogManager.getLogger(BitBucketServerProducer.class);

    @Autowired
    private CredentialManager credentialManager;

    @Autowired
    private MetricQueue metricQueue;

    @Autowired
    private BitBucketServerRestClient bitBucketServerRestClient;

    public BitBucketServerProducer() {
        // default constructor
    }

    @Override
    public void produce() {
        try {
            collectDailyCommitData();
        } catch (Exception e) {
            log.error("Error producing metric.", e);
        }
    }

    @Override
    public boolean checkConnection() {
        return bitBucketServerRestClient.checkConnection();
    }

    private void collectDailyCommitData() {
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
}
