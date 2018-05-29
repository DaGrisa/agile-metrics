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
    public boolean checkConnection() {
        try {
            return bitBucketServerRestClient.checkConnection();
        } catch (Exception e) {
            log.error("could not connect to BitBucket Server, check error message", e);
            return false;
        }
    }

    @Override
    public void produce() {
        try {
            collectDailyCommitData();
        } catch (Exception e) {
            log.error("Error producing metric.", e);
        }
    }

    private void collectDailyCommitData() {
        // get all commits per project / repository / author
        Collection<Project> projects = bitBucketServerRestClient.getProjects();
        log.debug(projects.size() + " projects found on BitBucket Server instance");
        for (Project project : projects) {
            log.debug("collecting data for project " + project.getName());
            // failsafe
            try {
                collectDailyCommitDataPerProject(project);
            } catch (Exception e) {
                log.error("error collecting daily commits for project " + project.getName(), e);
            }
            log.debug("finished collecting data for project " + project.getName());
        }
    }

    private void collectDailyCommitDataPerProject(Project project) {
        HashMap<String, Integer> commitsPerProject = new HashMap<>();
        HashMap<String, Integer> commitsPerAuthor = new HashMap<>();
        HashMap<String, Integer> commitsPerRepository = new HashMap<>();

        Date created = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(created);
        cal.add(Calendar.DATE, -1);
        Date from = cal.getTime();

        Collection<Repository> repositories = bitBucketServerRestClient.getRepositories(project.getKey());
        for (Repository repository : repositories) {
            // failsafe
            Collection<Commit> commits = null;
            try {
                commits = bitBucketServerRestClient.getCommits(project.getKey(), repository.getSlug(), from);
            } catch (Exception e) {
                log.error("error collecting commit data for repository " + repository.getName() + " in project " + project.getName(), e);
            }

            for (Commit commit : commits) {
                Integer commitsPerProjectCount = commitsPerProject.get(project.getName());
                if (commitsPerProjectCount == null) {
                    commitsPerProjectCount = 0;
                }
                commitsPerProject.put(project.getName(), commitsPerProjectCount + 1);

                Integer commitsPerAuthorCount = commitsPerAuthor.get(commit.getAuthor().getName());
                if (commitsPerAuthorCount == null) {
                    commitsPerAuthorCount = 0;
                }
                commitsPerAuthor.put(commit.getAuthor().getName(), commitsPerAuthorCount + 1);

                Integer commitsPerRepositoryCount = commitsPerRepository.get(repository.getName());
                if (commitsPerRepositoryCount == null) {
                    commitsPerRepositoryCount = 0;
                }
                commitsPerRepository.put(repository.getName(), commitsPerRepositoryCount + 1);
            }
        }

        enqueueMetrics(commitsPerProject, commitsPerAuthor, commitsPerRepository);
    }

    private void enqueueMetrics(HashMap<String, Integer> commitsPerProject, HashMap<String, Integer> commitsPerAuthor, HashMap<String, Integer> commitsPerRepository) {
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
