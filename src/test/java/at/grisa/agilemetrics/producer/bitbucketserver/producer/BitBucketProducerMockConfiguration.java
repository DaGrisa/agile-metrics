package at.grisa.agilemetrics.producer.bitbucketserver.producer;

import at.grisa.agilemetrics.cron.MetricQueue;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerRestClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class BitBucketProducerMockConfiguration {
    @Bean
    @Primary
    public BitBucketServerRestClient restClient() {
        return Mockito.mock(BitBucketServerRestClient.class);
    }

    @Bean
    @Primary
    public MetricQueue metricQueue() {
        return Mockito.mock(MetricQueue.class);
    }
}
