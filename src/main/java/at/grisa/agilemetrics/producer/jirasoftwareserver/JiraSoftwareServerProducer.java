package at.grisa.agilemetrics.producer.jirasoftwareserver;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.persistence.IVelocityRepository;
import at.grisa.agilemetrics.persistence.entity.Velocity;
import at.grisa.agilemetrics.producer.IProducer;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.*;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.RapidView;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.SprintReport;
import at.grisa.agilemetrics.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JiraSoftwareServerProducer implements IProducer {
    private static final Logger log = LogManager.getLogger(JiraSoftwareServerProducer.class);

    public static final String META_SPRINTNAME = "sprint";
    public static final String META_BOARDNAME = "board";
    public static final String META_RAPIDVIEWNAME = "rapidview";
    public static final String META_ISSUEKEY = "issue";
    public static final String META_SPRINTGOAL = "sprint-goal";
    public static final String META_STATUSCATEGORYKEY = "status-category";
    public static final String META_STATUSKEY = "status";

    @Autowired
    private JiraSoftwareServerRestClient jiraRestClient;

    @Autowired
    private MetricQueue metricQueue;

    @Autowired
    private IVelocityRepository velocityRepository;

    @Autowired
    private PropertyManager propertyManager;

    @Override
    public boolean checkConnection() {
        try {
            return jiraRestClient.checkConnection();
        } catch (Exception e) {
            log.error("could not connect to JIRA Software Server, check error message", e);
            return false;
        }
    }

    @Override
    public void produce() {
        ArrayList<Method> producingMethods = new ArrayList<>();
        try {
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceIssueVolume"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceCumulativeFlow"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceEstimatedStoryPoints"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceLeadTime"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceBugRate"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceBugCount"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceRecidivism"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceAcceptanceCriteriaVolatility"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceVelocity"));
            producingMethods.add(JiraSoftwareServerProducer.class.getMethod("produceIssueLabels"));
        } catch (NoSuchMethodException e) {
            log.error("Error reflecting producer methods.", e);
        }

        // failsafe producing
        for (Method method : producingMethods) {
            try {
                log.info("invoking producing method: " + method.getName());
                method.invoke(this);
                log.info("producing method completed: " + method.getName());
            } catch (Exception e) {
                log.error("Error in producing method " + method, e);
            }
        }

    }

    public void produceIssueVolume() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            Sprint activeSprint = jiraRestClient.getActiveSprint(scrumBoard.getId());

            if (activeSprint != null) {
                Integer issueVolumeValue = jiraRestClient.getSprintIssuesCount(scrumBoard.getId(), activeSprint.getId());
                HashMap<String, String> issueVolumeMeta = new HashMap<>();
                issueVolumeMeta.put(META_SPRINTNAME, activeSprint.getName());
                issueVolumeMeta.put(META_BOARDNAME, scrumBoard.getName());
                Metric issueVolume = new Metric(issueVolumeValue.doubleValue(), "Issue Volume", issueVolumeMeta);
                metricQueue.enqueueMetric(issueVolume);
            } else {
                log.info("no active sprint for scrum board " + scrumBoard.getName() + ", cancel producing issue volume");
            }
        }
    }

    public void produceCumulativeFlow() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            HashMap<String, Integer> statusCount = new HashMap<>();
            HashMap<String, Integer> statusCategoryCount = new HashMap<>();

            // collect status date
            Sprint activeSprint = jiraRestClient.getActiveSprint(scrumBoard.getId());
            if (activeSprint != null) {
                getStatus(scrumBoard, statusCount, statusCategoryCount, activeSprint);

                // enqueue status count
                for (Map.Entry<String, Integer> statusCountEntry : statusCount.entrySet()) {
                    HashMap<String, String> meta = new HashMap<>();
                    meta.put(META_STATUSKEY, statusCountEntry.getKey());
                    meta.put(META_BOARDNAME, scrumBoard.getName());
                    meta.put(META_SPRINTNAME, activeSprint.getName());
                    Metric cumulativeFlow = new Metric(statusCountEntry.getValue().doubleValue(), "Cumulative Flow - Status", meta);
                    metricQueue.enqueueMetric(cumulativeFlow);
                }

                // enqueue status category count
                for (Map.Entry<String, Integer> statusCategoryCountEntry : statusCategoryCount.entrySet()) {
                    HashMap<String, String> meta = new HashMap<>();
                    meta.put(META_STATUSCATEGORYKEY, statusCategoryCountEntry.getKey());
                    meta.put(META_BOARDNAME, scrumBoard.getName());
                    meta.put(META_SPRINTNAME, activeSprint.getName());
                    Metric cumulativeFlow = new Metric(statusCategoryCountEntry.getValue().doubleValue(), "Cumulative Flow - Status Category", meta);
                    metricQueue.enqueueMetric(cumulativeFlow);
                }
            } else {
                log.info("no active sprint for scrum board " + scrumBoard.getName() + ", cancel producing cumulative flow");
            }
        }
    }

    private void getStatus(Board scrumBoard, HashMap<String, Integer> statusCount, HashMap<String, Integer> statusCategoryCount, Sprint activeSprint) {
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
    }

    public void produceEstimatedStoryPoints() {
        for (RapidView rapidView : jiraRestClient.getRapidViewsGreenhopper()) {
            at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.Sprint activeSprint = jiraRestClient.getActiveSprintGreenhopper(rapidView.getId());
            if (activeSprint != null) {
                SprintReport sprintReport = jiraRestClient.getSprintReportGreenhopper(rapidView.getId(), activeSprint.getId());

                // enqueue completed issues estimate sum
                Integer completedIssuesEstimateSum = sprintReport.getContents().getCompletedIssuesEstimateSum().getValue();
                if (completedIssuesEstimateSum != null) {
                    HashMap<String, String> metaCompletedIssues = new HashMap<>();
                    metaCompletedIssues.put(META_RAPIDVIEWNAME, rapidView.getName());
                    metaCompletedIssues.put(META_SPRINTNAME, activeSprint.getName());
                    metricQueue.enqueueMetric(new Metric(completedIssuesEstimateSum.doubleValue(), "Completed Issues Estimate Sum", metaCompletedIssues));
                } else {
                    log.info("no completed issues story points on " + activeSprint.getName());
                }

                // enqueue not completed issues estimate sum
                Integer notCompletedIssuesEstimateSum = sprintReport.getContents().getIssuesNotCompletedEstimateSum().getValue();
                if (notCompletedIssuesEstimateSum != null) {
                    HashMap<String, String> metaNotCompletedIssues = new HashMap<>();
                    metaNotCompletedIssues.put(META_RAPIDVIEWNAME, rapidView.getName());
                    metaNotCompletedIssues.put(META_SPRINTNAME, activeSprint.getName());
                    metricQueue.enqueueMetric(new Metric(notCompletedIssuesEstimateSum.doubleValue(), "Not Completed Issues Estimate Sum", metaNotCompletedIssues));
                } else {
                    log.info("no not completed issues story points on " + activeSprint.getName());
                }
            } else {
                log.info("no active sprint for rapid view " + rapidView.getName() + ", cancel producing estimated story points");
            }
        }
    }

    public void produceLeadTime() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = addToJQL(jql, "resolutiondate > -1d");
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            for (Issue issue : issues) {
                ZonedDateTime created = issue.getFields().getCreated();
                ZonedDateTime resolutionDate = issue.getFields().getResolutiondate();

                if (created != null && resolutionDate != null) {
                    Long daysToFinish = Duration.between(created, resolutionDate).toDays();

                    HashMap<String, String> meta = new HashMap<>();
                    meta.put(META_BOARDNAME, scrumBoard.getName());
                    meta.put(META_ISSUEKEY, issue.getKey());

                    metricQueue.enqueueMetric(new Metric(daysToFinish.doubleValue(), "Lead Time", meta));
                } else {
                    log.error("created or resolution date empty on resolved issue " + issue.getKey() + ", created: " + created + ", resolution date: " + resolutionDate);
                    log.debug("used JQL: " + jql);
                }
            }
        }
    }

    private String addToJQL(String jql, String conditionToAdd) {
        if (jql.toLowerCase().contains("order by")) {
            jql = jql.toLowerCase().replace(" order by", ") order by");
        } else {
            jql = jql + ")";
        }
        jql = conditionToAdd + " AND (" + jql;
        return jql;
    }

    public void produceBugRate() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = addToJQL(jql, "created > -1d AND type = Bug"); // only show issues created in the last 24h
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            Integer newBugsCount = issues.size();

            HashMap<String, String> meta = new HashMap<>();
            meta.put(META_BOARDNAME, scrumBoard.getName());

            metricQueue.enqueueMetric(new Metric(newBugsCount.doubleValue(), "Bug Rate", meta));
        }
    }

    public void produceBugCount() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = addToJQL(jql, "type = Bug AND statusCategory != Done"); // only show issues created in the last 24h
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            Integer bugsCount = issues.size();

            HashMap<String, String> meta = new HashMap<>();
            meta.put(META_BOARDNAME, scrumBoard.getName());

            metricQueue.enqueueMetric(new Metric(bugsCount.doubleValue(), "Bug Count", meta));
        }
    }

    public void produceRecidivism() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = addToJQL(jql, "resolutiondate > -1d");
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            for (Issue issue : issues) {
                produceRecidivismFromIssue(issue, scrumBoard.getName());
            }
        }
    }

    private void produceRecidivismFromIssue(Issue issue, String scrumBoardName) {
        List<String> workflow = propertyManager.getJirasoftwareWorkflow();
        if (workflow.isEmpty()) {
            throw new IllegalStateException("no JIRA Software workflow defined in application.properties (producer.jirasoftware.workflow), cannot produce recidivism");
        }

        Issue issueWithChangelog = jiraRestClient.getIssue(issue.getId(), new QueryParam("expand", "changelog"));

        if (issueWithChangelog != null && issueWithChangelog.getChangelog() != null) {
            List<HistoryItem> statusChanges = getStatusChanges(issueWithChangelog);

            statusChanges.sort((HistoryItem item1, HistoryItem item2) -> item1.getCreated().compareTo(item2.getCreated()));

            Integer recidivismCount = 0;
            Integer lastStatusIndex = 0;
            for (HistoryItem item : statusChanges) {
                Integer actualStatusIndex = workflow.indexOf(item.getToString().toLowerCase());
                if (actualStatusIndex < lastStatusIndex) {
                    recidivismCount++;
                }
                lastStatusIndex = actualStatusIndex;
            }

            HashMap<String, String> meta = new HashMap<>();
            meta.put(META_BOARDNAME, scrumBoardName);
            meta.put(META_ISSUEKEY, issueWithChangelog.getKey());

            metricQueue.enqueueMetric(new Metric(recidivismCount.doubleValue(), "Recidivism", meta));
        } else {
            log.error("no changelog for issue " + issue.getKey());
        }
    }

    private List<HistoryItem> getStatusChanges(Issue issueWithChangelog) {
        List<HistoryItem> statusChanges = new LinkedList<>();
        for (History history : issueWithChangelog.getChangelog().getHistories()) {
            for (HistoryItem item : history.getItems()) {
                if (item.getField().equalsIgnoreCase("status")) {
                    item.setCreated(history.getCreated());
                    statusChanges.add(item);
                }
            }
        }

        return statusChanges;
    }

    public void produceAcceptanceCriteriaVolatility() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = addToJQL(jql, "resolutiondate > -1d");
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            String acceptanceCriteriaFieldName = propertyManager.getJirasoftwareAcceptanceCriteriaFieldName();
            if (acceptanceCriteriaFieldName == null) {
                throw new IllegalStateException("no JIRA Software acceptance criteria field name defined in application.properties (producer.jirasoftware.acceptanceCriteriaFieldName), cannot produce acceptance criteria volatility");
            }

            for (Issue issue : issues) {
                produceAcceptanceCriteriaVolatilityForIssue(issue, scrumBoard, acceptanceCriteriaFieldName);
            }
        }
    }

    private void produceAcceptanceCriteriaVolatilityForIssue(Issue issue, Board scrumBoard, String acceptanceCriteriaFieldName) {
        Issue issueChangelog = jiraRestClient.getIssue(issue.getId(), new QueryParam("expand", "changelog"));
        Integer acceptanceCriteriaChangeCounter = 0;

        for (History history : issueChangelog.getChangelog().getHistories()) {
            for (HistoryItem item : history.getItems()) {
                if (item.getField().equalsIgnoreCase(acceptanceCriteriaFieldName)) {
                    acceptanceCriteriaChangeCounter++;
                }
            }
        }

        HashMap<String, String> meta = new HashMap<>();
        meta.put(META_BOARDNAME, scrumBoard.getName());
        meta.put(META_ISSUEKEY, issueChangelog.getKey());

        metricQueue.enqueueMetric(new Metric(acceptanceCriteriaChangeCounter.doubleValue(), "Acceptance Criteria Volatility", meta));
    }

    public void produceVelocity() {
        for (RapidView rapidView : jiraRestClient.getRapidViewsGreenhopper()) {
            VelocityReport velocityReport = jiraRestClient.getVelocityReportGreenhopper(rapidView.getId());

            List<Velocity> velocities = velocityRepository.findByTeam(rapidView.getName());
            List<String> savedSprintNames = new LinkedList<>();
            if (velocities != null) {
                savedSprintNames = velocities.stream().map(Velocity::getSprint).collect(Collectors.toList());
                log.info("velocities found for sprints " + savedSprintNames);
            } else {
                log.warn("no saved velocities found, ignore if this is the first run");
            }

            List<VelocityStats> velocityStats = new LinkedList<>();
            Map<Long, Sprint> sprints = new HashMap<>();

            if (velocityReport != null) {
                for (Sprint sprint : velocityReport.getSprints()) {
                    if (sprint.getState().equalsIgnoreCase("closed") && !savedSprintNames.contains(sprint.getName())) {
                        VelocityStats velocityStat = velocityReport.getVelocityStatEntries().get(sprint.getId().toString());
                        velocityStat.setSprintId(sprint.getId());

                        velocityStats.add(velocityStat);
                        sprints.put(sprint.getId(), sprint);
                    }
                }
            } else {
                log.info("velocity report for rapidview " + rapidView.getName() + " is null");
            }

            enqueueVelocityMetrics(rapidView, velocityStats, sprints);
        }

    }

    private void enqueueVelocityMetrics(RapidView rapidView, List<VelocityStats> velocityStats, Map<Long, Sprint> sprints) {
        for (VelocityStats velocityStat : velocityStats) {
            String sprintName = sprints.get(velocityStat.getSprintId()).getName();
            String sprintGoal = sprints.get(velocityStat.getSprintId()).getGoal();

            HashMap<String, String> meta = new HashMap<>();
            meta.put(META_RAPIDVIEWNAME, rapidView.getName());
            meta.put(META_SPRINTNAME, sprintName);
            meta.put(META_SPRINTGOAL, sprintGoal);

            metricQueue.enqueueMetric(new Metric(velocityStat.getEstimated().getValue().doubleValue(), "Velocity - Estimated", meta));
            metricQueue.enqueueMetric(new Metric(velocityStat.getCompleted().getValue().doubleValue(), "Velocity - Completed", meta));

            Velocity velocity = new Velocity(rapidView.getName(), sprintName, sprintGoal, velocityStat.getEstimated().getValue(), velocityStat.getCompleted().getValue());
            velocityRepository.save(velocity);
        }
    }

    public void produceIssueLabels() {
        for (Board scrumBoard : jiraRestClient.getScrumBoards()) {
            String jql = jiraRestClient.getScrumBoardJQLFilter(scrumBoard.getId());
            jql = addToJQL(jql, "resolutiondate > -1d");
            Collection<Issue> issues = jiraRestClient.getIssuesByJQL(jql);

            if (issues != null) {
                for (Issue issue : issues) {
                    if (issue.getFields().getLabels() != null && issue.getFields().getLabels().length > 0) {
                        HashMap<String, String> meta = new HashMap<>();
                        meta.put(META_BOARDNAME, scrumBoard.getName());
                        meta.put(META_ISSUEKEY, issue.getKey());

                        Double tagCount = new Double(issue.getFields().getLabels().length);
                        Set<String> tags = new HashSet<>(Arrays.asList(issue.getFields().getLabels()));
                        metricQueue.enqueueMetric(new Metric(tagCount, "Issue Labels", meta, tags));
                    } else {
                        log.debug("no labels for issue " + issue.getKey());
                    }
                }
            } else {
                log.info("no issues for scrumboard " + scrumBoard.getName());
            }
        }
    }
}
