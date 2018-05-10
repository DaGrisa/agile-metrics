package at.grisa.agilemetrics.consumer;

import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchRestClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ElasticSearchRestClientMockConfiguration {
    @Bean
    @Primary
    public ElasticSearchRestClient restClient() {
        return Mockito.mock(ElasticSearchRestClient.class);
    }
}
