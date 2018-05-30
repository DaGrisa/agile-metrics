package at.grisa.agilemetrics.cron;

import at.grisa.agilemetrics.persistence.IStatisticRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class CronObserverMockConfiguration {
    @Bean
    @Primary
    public MetricQueue metricQueue() {
        return Mockito.mock(MetricQueue.class);
    }

    @Bean
    @Primary
    public IStatisticRepository statisticRepository() {
        return Mockito.mock(IStatisticRepository.class);
    }
}
