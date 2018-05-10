package at.grisa.agilemetrics.producer.jirasoftwareserver.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.persistence.IVelocityRepository;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerRestClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class JiraSoftwareServerProducerMockConfiguration {
    @Bean
    @Primary
    public JiraSoftwareServerRestClient restClient() {
        return Mockito.mock(JiraSoftwareServerRestClient.class);
    }

    @Bean
    @Primary
    public MetricQueue metricQueue() {
        return Mockito.mock(MetricQueue.class);
    }

    @Bean
    @Primary
    public IVelocityRepository velocityRepository() {
        return Mockito.mock(IVelocityRepository.class);
    }
}
