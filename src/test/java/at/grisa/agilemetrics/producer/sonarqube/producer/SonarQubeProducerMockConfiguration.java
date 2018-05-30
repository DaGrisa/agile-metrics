package at.grisa.agilemetrics.producer.sonarqube.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeRestClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class SonarQubeProducerMockConfiguration {
    @Bean
    @Primary
    public SonarQubeRestClient restClient() {
        return Mockito.mock(SonarQubeRestClient.class);
    }

    @Bean
    @Primary
    public MetricQueue metricQueue() {
        return Mockito.mock(MetricQueue.class);
    }
}
