package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.Value;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.Contents;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.RapidView;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.Sprint;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentities.greenhopper.SprintReport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class JiraSoftwareServerProducerEstimatedStoryPoints {
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    private MetricQueue metricQueue;
    private RapidView rapidView;
    private Sprint activeSprint;

    private final Long rapidViewId = 123L;
    private final String rapidViewName = "rapid view name";
    private final Long sprintId = 456L;
    private final String sprintName = "active sprint name";
    private final Integer completedEstimatedStoryPoints = 25;
    private final Integer notCompletedEstimatedStoryPoints = 15;

    @Before
    public void createMocks() {
        rapidView = new RapidView();
        rapidView.setId(rapidViewId);
        rapidView.setName(rapidViewName);

        jiraSoftwareServerRestClient = mock(JiraSoftwareServerRestClient.class);
        when(jiraSoftwareServerRestClient.getRapidViewsGreenhopper()).thenReturn(Arrays.asList(rapidView));

        activeSprint = new Sprint();
        activeSprint.setId(sprintId);
        activeSprint.setName(sprintName);
        when(jiraSoftwareServerRestClient.getActiveSprintGreenhopper(rapidView.getId())).thenReturn(activeSprint);

        Contents contents = new Contents();
        Value completedEstimate = new Value();
        completedEstimate.setValue(completedEstimatedStoryPoints);
        contents.setCompletedIssuesEstimateSum(completedEstimate);
        Value notCompletedEstimate = new Value();
        notCompletedEstimate.setValue(notCompletedEstimatedStoryPoints);
        contents.setIssuesNotCompletedEstimateSum(notCompletedEstimate);
        SprintReport sprintReport = new SprintReport();
        sprintReport.setContents(contents);
        when(jiraSoftwareServerRestClient.getSprintReportGreenhopper(rapidView.getId(), activeSprint.getId())).thenReturn(sprintReport);

        metricQueue = mock(MetricQueue.class);
    }

    @Test
    public void produceIssueVolumeTest() {
        JiraSoftwareServerProducer jiraSoftwareServerProducer = new JiraSoftwareServerProducer();
        jiraSoftwareServerProducer.produceEstimatedStoryPoints(metricQueue, jiraSoftwareServerRestClient);

        // enqueue completed issues estimate sum
        Integer completedIssuesEstimateSum = completedEstimatedStoryPoints;
        HashMap<String, String> metaCompletedIssues = new HashMap<>();
        metaCompletedIssues.put("rapidview", rapidView.getName());
        metaCompletedIssues.put("sprint", activeSprint.getName());
        Metric completedIssuesEstimateSumMetric = new Metric(completedIssuesEstimateSum.doubleValue(), "Completed Issues Estimate Sum", metaCompletedIssues);

        // enqueue not completed issues estimate sum
        Integer notCompletedIssuesEstimateSum = notCompletedEstimatedStoryPoints;
        HashMap<String, String> metaNotCompletedIssues = new HashMap<>();
        metaNotCompletedIssues.put("rapidview", rapidView.getName());
        metaNotCompletedIssues.put("sprint", activeSprint.getName());
        Metric notCompletedIssuesEstimateSumMetric = new Metric(notCompletedIssuesEstimateSum.doubleValue(), "Not Completed Issues Estimate Sum", metaNotCompletedIssues);

        verify(metricQueue).enqueueMetric(completedIssuesEstimateSumMetric);
        verify(metricQueue).enqueueMetric(notCompletedIssuesEstimateSumMetric);
    }
}
