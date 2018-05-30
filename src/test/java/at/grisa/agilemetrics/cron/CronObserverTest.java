package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchConsumer;
import at.grisa.agilemetrics.entity.Metric;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeProducer;
import at.grisa.agilemetrics.util.CredentialManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CronObserver.class, CronObserverMockConfiguration.class, ApplicationConfig.class, CredentialManager.class})
public class CronObserverTest {
    @Autowired
    private CronObserver cronObserver;
    @Autowired
    private MetricQueue metricQueue;

    private BitBucketServerProducer bitBucketServerProducer;
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    private SonarQubeProducer sonarQubeProducer;
    private ElasticSearchConsumer elasticSearchConsumer;
    private final Metric metric = new Metric(1.2, "metric", null);

    @Before
    public void createMocks() {
        bitBucketServerProducer = mock(BitBucketServerProducer.class);
        cronObserver.registerProducer(bitBucketServerProducer);
        jiraSoftwareServerProducer = mock(JiraSoftwareServerProducer.class);
        cronObserver.registerProducer(jiraSoftwareServerProducer);
        sonarQubeProducer = mock(SonarQubeProducer.class);
        cronObserver.registerProducer(sonarQubeProducer);

        elasticSearchConsumer = mock(ElasticSearchConsumer.class);
        cronObserver.registerConsumer(elasticSearchConsumer);
        when(metricQueue.dequeueMetric()).thenAnswer(new Answer() {
            private int callCount = 0;

            public Object answer(InvocationOnMock invocation) {
                if (callCount++ == 0) {
                    return metric;
                }
                return null;
            }
        }); // return metric on first call only
    }

    @Test
    public void cronObserverTest() {
        cronObserver.activateProducerDaily();
        verify(bitBucketServerProducer).produce();
        verify(jiraSoftwareServerProducer).produce();
        verify(sonarQubeProducer).produce();

        cronObserver.activateConsumer();
        verify(elasticSearchConsumer).consume(metric);
    }
}
