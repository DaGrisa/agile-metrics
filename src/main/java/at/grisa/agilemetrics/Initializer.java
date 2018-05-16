package at.grisa.agilemetrics;

import at.grisa.agilemetrics.consumer.elasticsearch.ElasticSearchConsumer;
import at.grisa.agilemetrics.cron.CronObserver;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerProducer;
import at.grisa.agilemetrics.producer.jirasoftwareserver.JiraSoftwareServerProducer;
import at.grisa.agilemetrics.producer.sonarqube.SonarQubeProducer;
import at.grisa.agilemetrics.util.CredentialManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Initializer {
    private static final Logger log = LogManager.getLogger(Initializer.class);

    @Autowired
    private CredentialManager credentialManager;
    @Autowired
    private CronObserver cronObserver;

    /**
     * Consumer
     */
    @Autowired
    private ElasticSearchConsumer elasticSearchConsumer;

    /**
     * Producer
     */
    @Autowired
    private BitBucketServerProducer bitBucketServerProducer;
    @Autowired
    private JiraSoftwareServerProducer jiraSoftwareServerProducer;
    @Autowired
    private SonarQubeProducer sonarQubeProducer;

    @EventListener
    public void initApplication(ContextRefreshedEvent event) {
        log.info("Initializing application");

        if (credentialManager.isElasticsearchActive() && elasticSearchConsumer.checkConnection()) {
            log.info("Elasticsearch configuration detected, registering as consumer.");
            cronObserver.registerConsumer(elasticSearchConsumer);
        }

        if (credentialManager.isBitbucketserverActive() && bitBucketServerProducer.checkConnection()) {
            log.info("Bitbucket configuration detected, registering as producer.");
            cronObserver.registerProducer(bitBucketServerProducer);
        }

        if (credentialManager.isJirasoftwareActive() && jiraSoftwareServerProducer.checkConnection()) {
            log.info("Jira Software configuration detected, registering as producer.");
            cronObserver.registerProducer(jiraSoftwareServerProducer);
        }

        if (credentialManager.isSonarqubeActive() && sonarQubeProducer.checkConnection()) {
            log.info("SonarQube configuration detected, registering as producer.");
            cronObserver.registerProducer(sonarQubeProducer);
        }
    }
}
