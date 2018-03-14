package at.grisa.agilemetrics.producer.bitbucketserver;

import at.grisa.agilemetrics.cron.MeasurementQueue;
import at.grisa.agilemetrics.entity.Measurement;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Commit;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Project;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.Repository;
import at.grisa.agilemetrics.util.CredentialManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

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
    public void produce(MeasurementQueue measurementQueue) {
        collectDailyCommits(measurementQueue);
    }

    private void collectDailyCommits(MeasurementQueue measurementQueue) {
        Date created = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(created);
        cal.add(Calendar.DATE, -1);
        Date from = cal.getTime();

        Collection<Measurement> measurements = new LinkedList<>();

        Collection<Project> projects = bitBucketServerRestClient.getProjects();
        for (Project project : projects) {
            Collection<Repository> repositories = bitBucketServerRestClient.getRepositories(project.getKey());
            for (Repository repository : repositories) {
                Collection<Commit> commits = bitBucketServerRestClient.getCommits(project.getKey(), repository.getSlug(), from);
                for (Commit commit : commits) {
                    measurements.add(new Measurement("Commit to BitBucket Server", project.getName(), commit.getAuthor().getName(), 1.0, commit.getAuthorTimestamp(), created, repository.getName()));
                }
            }
        }

        for (Measurement measurement : measurements) {
            measurementQueue.enqueueMesurement(measurement);
        }
    }

    private void collectDailyChangedLines(MeasurementQueue measurementQueue) {

    }

    private void collectDailyNewPullRequests(MeasurementQueue measurementQueue) {

    }

    private void collectDailyActivePullRequests(MeasurementQueue measurementQueue) {

    }
}
