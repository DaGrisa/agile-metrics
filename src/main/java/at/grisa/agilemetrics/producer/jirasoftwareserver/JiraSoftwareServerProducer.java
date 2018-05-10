package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.cron.TimeSpan;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.persistence.IVelocityRepository;
import at.grisa.agilemetrics.persistence.entity.Velocity;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.*;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.RapidView;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.SprintReport;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JiraSoftwareServerProducer implements IProducer {
    @Autowired
    private JiraSoftwareServerRestClient jiraRestClient;

    @Autowired
    private MetricQueue metricQueue;

    @Autowired
    private IVelocityRepository velocityRepository;

    @Override
    public void produce(TimeSpan timespan) {
        CredentialManager credentialManager = new CredentialManager();

        switch (timespan) {
            case DAILY:
                produceIssueVolume();
                produceCumulativeFlow();
                produceEstimatedStoryPoints();
                produceLeadTime();
                produceBugRate();
                produceRecidivism();
                produceAcceptanceCriteriaVolatility();
                produceVelocity();
                produceIssueLabels();
                break;
        }
    }

    public void produceIssueVolume() {
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

    public void produceCumulativeFlow() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            HashMap<String, Integer> statusCount = new HashMap<>();
            HashMap<String, Integer> statusCategoryCount = new HashMap<>();

            // collect status date
            Sprint activeSprint = jiraRestClient.getActiveSprint(scrumBoard.getId());
            for (Issue issue : jiraRestClient.getSprintIssuesStatus(scrumBoard.getId(), activeSprint.getId())) {
                String issueStatus = issue.getFields().getStatus().getName();
                Integer actualStatusCount = statusCount.get(issueStatus);
                if (actualStatusCount == null) {
                    actualStatusCount = 0;
                }
                statusCount.put(issueStatus, actualStatusCount + 1);

                String issueSatusCategory = issue.getFields().getStatus().getStatusCategory().getName();
                Integer actualStatusCategoryCount = statusCategoryCount.get(issueSatusCategory);
                if (actualStatusCategoryCount == null) {
                    actualStatusCategoryCount = 0;
                }
                statusCategoryCount.put(issueSatusCategory, actualStatusCategoryCount + 1);
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

    public void produceEstimatedStoryPoints() {
        for (RapidView rapidView : jiraRestClient.getRapidViewsGreenhopper()) {
            HashMap<String, Integer> statusCount = new HashMap<>();
            HashMap<String, Integer> statusCategoryCount = new HashMap<>();

            at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.Sprint activeSprint = jiraRestClient.getActiveSprintGreenhopper(rapidView.getId());
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

    public void produceLeadTime() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = "resolutiondate > -1d AND " + jql; // only show resolved issues from last day
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            for (Issue issue : issues) {
                ZonedDateTime start = issue.getFields().getCreated();
                ZonedDateTime end = issue.getFields().getResolutiondate();

                Long daysToFinish = Duration.between(start, end).toDays();

                HashMap<String, String> meta = new HashMap<>();
                meta.put("scrum-board", scrumBoard.getName());
                meta.put("issue", issue.getKey());

                metricQueue.enqueueMetric(new Metric(daysToFinish.doubleValue(), "Lead Time", meta));
            }
        }
    }

    public void produceBugRate() {
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

    public void produceRecidivism() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = "resolutiondate > -1d AND " + jql; // only show resolved issues from last day
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            PropertyManager propertyManager = new PropertyManager();
            List<String> workflow = propertyManager.getJirasoftwareWorkflow();

            for (Issue issue : issues) {
                Issue issueChangelog = jiraRestClient.getIssue(issue.getId(), new QueryParam("expand", "changelog"));
                List<HistoryItem> statusChanges = new LinkedList<>();

                for (History history : issueChangelog.getChangelog().getHistories()) {
                    for (HistoryItem item : history.getItems()) {
                        if (item.getField().toLowerCase().equals("status")) {
                            item.setCreated(history.getCreated());
                            statusChanges.add(item);
                        }
                    }
                }

                statusChanges.sort((HistoryItem item1, HistoryItem item2) -> item1.getCreated().compareTo(item2.getCreated()));

                Integer recidivismCount = 0;
                Integer lastStatusIndex = 0;
                for (HistoryItem item : statusChanges) {
                    Integer actualStatusIndex = workflow.indexOf(item.getToString());
                    if (actualStatusIndex < lastStatusIndex) {
                        recidivismCount++;
                    }
                    lastStatusIndex = actualStatusIndex;
                }

                HashMap<String, String> meta = new HashMap<>();
                meta.put("scrum-board", scrumBoard.getName());
                meta.put("issue", issueChangelog.getKey());

                metricQueue.enqueueMetric(new Metric(recidivismCount.doubleValue(), "Recidivism", meta));
            }
        }
    }

    public void produceAcceptanceCriteriaVolatility() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = "resolutiondate > -1d AND " + jql; // only show resolved issues from last day
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            PropertyManager propertyManager = new PropertyManager();
            String acceptanceCriteriaFieldName = propertyManager.getJirasoftwareAcceptanceCriteriaFieldName();

            for (Issue issue : issues) {
                Issue issueChangelog = jiraRestClient.getIssue(issue.getId(), new QueryParam("expand", "changelog"));
                Integer acceptanceCriteriaChangeCounter = -1;

                for (History history : issueChangelog.getChangelog().getHistories()) {
                    for (HistoryItem item : history.getItems()) {
                        if (item.getField().toLowerCase().equals(acceptanceCriteriaFieldName)) {
                            acceptanceCriteriaChangeCounter++;
                        }
                    }
                }

                HashMap<String, String> meta = new HashMap<>();
                meta.put("scrum-board", scrumBoard.getName());
                meta.put("issue", issueChangelog.getKey());

                metricQueue.enqueueMetric(new Metric(acceptanceCriteriaChangeCounter.doubleValue(), "Acceptance Criteria Volatility", meta));
            }
        }
    }

    public void produceVelocity() {
        for (RapidView rapidView : jiraRestClient.getRapidViewsGreenhopper()) {
            VelocityReport velocityReport = jiraRestClient.getVelocityReportGreenhopper(rapidView.getId());

            List<Velocity> velocities = velocityRepository.findByTeam(rapidView.getName());
            List<String> savedSprintNames = velocities.stream().map(Velocity::getSprint).collect(Collectors.toList());

            List<VelocityStats> velocityStats = new LinkedList<>();
            Map<Long, Sprint> sprints = new HashMap<>();

            for (Sprint sprint : velocityReport.getSprints()) {
                if (sprint.getState().toLowerCase().equals("closed")) {
                    if (!savedSprintNames.contains(sprint.getName())) {
                        VelocityStats velocityStat = velocityReport.getVelocityStatEntries().get(sprint.getId().toString());
                        velocityStat.setSprintId(sprint.getId());

                        velocityStats.add(velocityStat);
                        sprints.put(sprint.getId(), sprint);
                    }
                }
            }

            for (VelocityStats velocityStat : velocityStats) {
                String sprintName = sprints.get(velocityStat.getSprintId()).getName();
                String sprintGoal = sprints.get(velocityStat.getSprintId()).getGoal();

                HashMap<String, String> meta = new HashMap<>();
                meta.put("rapidview", rapidView.getName());
                meta.put("sprint", sprintName);
                meta.put("sprint-goal", sprintGoal);

                metricQueue.enqueueMetric(new Metric(velocityStat.getEstimated().getValue().doubleValue(), "Velocity - Estimated", meta));
                metricQueue.enqueueMetric(new Metric(velocityStat.getCompleted().getValue().doubleValue(), "Velocity - Completed", meta));

                Velocity velocity = new Velocity(rapidView.getName(), sprintName, sprintGoal, velocityStat.getEstimated().getValue(), velocityStat.getCompleted().getValue());
                velocityRepository.save(velocity);
            }
        }

    }

    public void produceIssueLabels() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = "resolutiondate > -1d AND " + jql; // only show resolved issues from last day
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            for (Issue issue : issues) {
                if (issue.getFields().getLabels() != null && issue.getFields().getLabels().length > 0) {
                    HashMap<String, String> meta = new HashMap<>();
                    meta.put("scrum-board", scrumBoard.getName());
                    meta.put("issue", issue.getKey());

                    Double tagCount = new Double(issue.getFields().getLabels().length);
                    Set<String> tags = new HashSet<String>(Arrays.asList(issue.getFields().getLabels()));
                    metricQueue.enqueueMetric(new Metric(tagCount, "Issue Labels", meta, tags));
                }
            }
        }
    }
}
