package at.grisa.agilemetrics.producer.sonarqube.producer;

import at.grisa.agilemetrics.ApplicationConfig;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeProducer;
import at.grisa.agilemetrics.util.CredentialManager;
import at.grisa.agilemetrics.util.PropertyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SonarQubeProducer.class, SonarQubeProducerMockConfiguration.class, ApplicationConfig.class, CredentialManager.class, PropertyManager.class})
public class SonarQubeProducerMetricsNoPropertiesTest {
    @Autowired
    private SonarQubeProducer sonarQubeProducer;

    @Test(expected = IllegalStateException.class)
    public void metricsTest() {
        sonarQubeProducer.produce();
    }
}
