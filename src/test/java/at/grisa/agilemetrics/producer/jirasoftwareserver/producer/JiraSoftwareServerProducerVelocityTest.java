package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.persistence.IVelocityRepository;
import at.grisa.agilemetrics.persistence.entity.Velocity;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Sprint;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Value;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.VelocityReport;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.VelocityStats;
import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper.RapidView;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JiraSoftwareServerProducer.class, CredentialManager.class, PropertyManager.class, JiraSoftwareServerProducerMockConfiguration.class, ApplicationConfig.class})
public class JiraSoftwareServerProducerVelocityTest {
    @Autowired
    private JiraSoftwareServerRestClient jiraSoftwareServerRestClient;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private MetricQueue metricQueue;
    @Autowired
    private IVelocityRepository velocityRepository;

    private RapidView rapidView;
    private VelocityReport velocityReport;

    private final Long rapidViewId = 123L;
    private final String rapidViewName = "rapid view name";
    private final String sprintNameOldAlreadySaved = "very old sprint";
    private final String sprintNameOld = "old sprint";
    private final String sprintGoalOld = "old sprint goal";
    private final String sprintNameActive = "active sprint";
    private final Long sprintIdOld = 1L;
    private final Integer completedPoints = 37;
    private final Integer estimatedPoints = 42;
    private Sprint sprintOld;

    @Before
    public void createMocks() {
        rapidView = new RapidView();
        rapidView.setId(rapidViewId);
        rapidView.setName(rapidViewName);
        when(jiraSoftwareServerRestClient.getRapidViewsGreenhopper()).thenReturn(Arrays.asList(rapidView));

        velocityReport = new VelocityReport();
        Sprint sprintOldAlreadySaved = new Sprint();
        sprintOldAlreadySaved.setName(sprintNameOldAlreadySaved);
        sprintOldAlreadySaved.setState("closed");
        sprintOld = new Sprint();
        sprintOld.setName(sprintNameOld);
        sprintOld.setState("closed");
        sprintOld.setId(sprintIdOld);
        sprintOld.setGoal(sprintGoalOld);
        Sprint sprintActive = new Sprint();
        sprintActive.setName(sprintNameActive);
        sprintActive.setState("active");
        velocityReport.setSprints(new Sprint[]{sprintOldAlreadySaved, sprintOld, sprintActive});
        Map<String, VelocityStats> velocityStatsMap = new HashMap<>();
        VelocityStats velocityStats = new VelocityStats();
        velocityStats.setSprintId(sprintIdOld);
        velocityStats.setCompleted(new Value(completedPoints));
        velocityStats.setEstimated(new Value(estimatedPoints));
        velocityStatsMap.put(sprintIdOld.toString(), velocityStats);
        velocityReport.setVelocityStatEntries(velocityStatsMap);
        when(jiraSoftwareServerRestClient.getVelocityReportGreenhopper(rapidView.getId())).thenReturn(velocityReport);

        Velocity velocity = new Velocity();
        velocity.setSprint(sprintNameOldAlreadySaved);
        when(velocityRepository.findByTeam(rapidViewName)).thenReturn(Arrays.asList(velocity));
    }

    @Test
    public void produceVelocityTest() {
        jiraSoftwareServerProducer.produceVelocity();

        HashMap<String, String> metaVelocity = new HashMap<>();
        metaVelocity.put("rapidview", rapidView.getName());
        metaVelocity.put("sprint", sprintOld.getName());
        metaVelocity.put("sprint-goal", sprintOld.getGoal());

        Metric velocityEstimatedMetric = new Metric(estimatedPoints.doubleValue(), "Velocity - Estimated", metaVelocity);
        Metric velocityCompletedMetric = new Metric(completedPoints.doubleValue(), "Velocity - Completed", metaVelocity);

        verify(metricQueue).enqueueMetric(velocityEstimatedMetric);
        verify(metricQueue).enqueueMetric(velocityCompletedMetric);
    }
}
