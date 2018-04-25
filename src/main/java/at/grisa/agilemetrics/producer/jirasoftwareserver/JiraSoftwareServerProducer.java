package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.cron.TimeSpan;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Board;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Issue;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Sprint;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.RapidView;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.SprintReport;
import at.grisa.agilemetrics.util.CredentialManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JiraSoftwareServerProducer implements IProducer {

    @Override
    public void produce(MetricQueue metricQueue, TimeSpan timespan) {
        CredentialManager credentialManager = new CredentialManager();

        JiraSoftwareServerRestClient jiraRestClient = new JiraSoftwareServerRestClient(
                credentialManager.getBitbucketserverBaseUrl(),
                credentialManager.getBitbucketserverUsername(),
                credentialManager.getBitbucketserverPassword()
        );

        produceIssueVolume(metricQueue, jiraRestClient);
        produceCumulativeFlow(metricQueue, jiraRestClient);
        produceEstimatedStoryPoints(metricQueue, jiraRestClient);
        produceLeadTime(metricQueue, jiraRestClient);
        produceBugRate(metricQueue, jiraRestClient);
    }

    private void produceIssueVolume(MetricQueue metricQueue, JiraSoftwareServerRestClient jiraRestClient) {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            Sprint activeSprint = jiraRestClient.getActiveSprint(scrumBoard.getId());

            Integer issueVolumeValue = jiraRestClient.getSprintIssuesCount(scrumBoard.getId(), activeSprint.getId());
            HashMap<String, String> issueVolumeMeta = new HashMap<>();
            issueVolumeMeta.put("sprint", activeSprint.getName());
            issueVolumeMeta.put("board", scrumBoard.getName());
            Metric issueVolume = new Metric(issueVolumeValue.doubleValue(), "Issue Volume", issueVolumeMeta);
            metricQueue.enqueueMetric(issueVolume);
        }
    }

    private void produceCumulativeFlow(MetricQueue metricQueue, JiraSoftwareServerRestClient jiraRestClient) {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            HashMap<String, Integer> statusCount = new HashMap<>();
            HashMap<String, Integer> statusCategoryCount = new HashMap<>();

            // collect status date
            Sprint activeSprint = jiraRestClient.getActiveSprint(scrumBoard.getId());
            for (Issue issue : jiraRestClient.getSprintIssuesStatus(scrumBoard.getId(), activeSprint.getId())) {
                String issueStatus = issue.getFields().getStatus().getName();
                statusCount.put(issueStatus, statusCount.get(issueStatus) + 1);

                String issueSatusCategory = issue.getFields().getStatus().getStatusCategory().getName();
                statusCategoryCount.put(issueSatusCategory, statusCategoryCount.get(issueSatusCategory) + 1);
            }

            // enqueue status count
            for (Map.Entry<String, Integer> statusCountEntry : statusCount.entrySet()) {
                HashMap<String, String> meta = new HashMap<>();
                meta.put("status", statusCountEntry.getKey());
                meta.put("board", scrumBoard.getName());
                meta.put("sprint", activeSprint.getName());
                Metric cumulativeFlow = new Metric(statusCountEntry.getValue().doubleValue(), "Cumulative Flow - Status", meta);
                metricQueue.enqueueMetric(cumulativeFlow);
            }

            // enqueue status category count
            for (Map.Entry<String, Integer> statusCategoryCountEntry : statusCategoryCount.entrySet()) {
                HashMap<String, String> meta = new HashMap<>();
                meta.put("status-category", statusCategoryCountEntry.getKey());
                meta.put("board", scrumBoard.getName());
                meta.put("sprint", activeSprint.getName());
                Metric cumulativeFlow = new Metric(statusCategoryCountEntry.getValue().doubleValue(), "Cumulative Flow - Status Category", meta);
                metricQueue.enqueueMetric(cumulativeFlow);
            }
        }
    }

    private void produceEstimatedStoryPoints(MetricQueue metricQueue, JiraSoftwareServerRestClient jiraRestClient) {
        for (RapidView rapidView : jiraRestClient.getRapidViewsGreenhopper()) {
            HashMap<String, Integer> statusCount = new HashMap<>();
            HashMap<String, Integer> statusCategoryCount = new HashMap<>();

            at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.Sprint activeSprint = jiraRestClient.getActiveSprintGreenhopper(rapidView.getId());
            SprintReport sprintReport = jiraRestClient.getSprintReportGreenhopper(rapidView.getId(), activeSprint.getId());

            // enqueue completed issues estimate sum
            Integer completedIssuesEstimateSum = sprintReport.getContents().getCompletedIssuesEstimateSum().getValue();
            HashMap<String, String> metaCompletedIssues = new HashMap<>();
            metaCompletedIssues.put("rapidview", rapidView.getName());
            metaCompletedIssues.put("sprint", activeSprint.getName());
            metricQueue.enqueueMetric(new Metric(completedIssuesEstimateSum.doubleValue(), "Completed Issues Estimate Sum", metaCompletedIssues));

            // enqueue not completed issues estimate sum
            Integer notCompletedIssuesEstimateSum = sprintReport.getContents().getIssuesNotCompletedEstimateSum().getValue();
            HashMap<String, String> metaNotCompletedIssues = new HashMap<>();
            metaNotCompletedIssues.put("rapidview", rapidView.getName());
            metaNotCompletedIssues.put("sprint", activeSprint.getName());
            metricQueue.enqueueMetric(new Metric(notCompletedIssuesEstimateSum.doubleValue(), "Not Completed Issues Estimate Sum", metaNotCompletedIssues));
        }
    }

    private void produceLeadTime(MetricQueue metricQueue, JiraSoftwareServerRestClient jiraRestClient) {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = "resolutiondate > -1d AND " + jql; // only show resolved issues from last day
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            for (Issue issue : issues) {
                Instant start = issue.getFields().getCreated();
                Instant end = issue.getFields().getResolutiondate();

                Long daysToFinish = Duration.between(start, end).toDays();

                HashMap<String, String> meta = new HashMap<>();
                meta.put("scrum-board", scrumBoard.getName());
                meta.put("issue", issue.getKey());

                metricQueue.enqueueMetric(new Metric(daysToFinish.doubleValue(), "Lead Time", meta));
            }
        }
    }

    private void produceBugRate(MetricQueue metricQueue, JiraSoftwareServerRestClient jiraRestClient) {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = "resolutiondate > -1d AND type = Bug AND " + jql; // only show resolved issues from last day
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            Integer newBugsCount = issues.size();

            HashMap<String, String> meta = new HashMap<>();
            meta.put("scrum-board", scrumBoard.getName());

            metricQueue.enqueueMetric(new Metric(newBugsCount.doubleValue(), "Bug Rate", meta));
        }
    }
}
