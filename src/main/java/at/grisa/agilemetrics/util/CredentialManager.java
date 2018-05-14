package at.grisa.agilemetrics.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CredentialManager {
    @Value("${producer.bitbucketserver.baseUrl:@null}")
    private String bitbucketserverBaseUrl;
    @Value("${producer.bitbucketserver.username:@null}")
    private String bitbucketserverUsername;
    @Value("${producer.bitbucketserver.password:@null}")
    private String bitbucketserverPassword;

    @Value("${producer.jirasoftware.baseUrl:@null}")
    private String jirasoftwareBaseUrl;
    @Value("${producer.jirasoftware.username:@null}")
    private String jirasoftwareUsername;
    @Value("${producer.jirasoftware.password:@null}")
    private String jirasoftwarePassword;

    @Value("${producer.sonarqube.baseUrl:@null}")
    private String sonarqubeBaseUrl;
    @Value("${producer.sonarqube.username:@null}")
    private String sonarqubeUsername;
    @Value("${producer.sonarqube.password:@null}")
    private String sonarqubePassword;

    @Value("${consumer.elasicsearch.baseUrl:@null}")
    private String elasicsearchBaseUrl;

    public CredentialManager() {
        // default constructor
    }

    public String getBitbucketserverBaseUrl() {
        return bitbucketserverBaseUrl;
    }
    public String getBitbucketserverUsername() {
        return bitbucketserverUsername;
    }
    public String getBitbucketserverPassword() {
        return bitbucketserverPassword;
    }

    public boolean isBitbucketserverActive() {
        return this.bitbucketserverBaseUrl != null && this.bitbucketserverUsername != null && this.bitbucketserverPassword != null;
    }

    public String getJirasoftwareBaseUrl() {
        return jirasoftwareBaseUrl;
    }
    public String getJirasoftwareUsername() {
        return jirasoftwareUsername;
    }
    public String getJirasoftwarePassword() {
        return jirasoftwarePassword;
    }

    public boolean isJirasoftwareActive() {
        return this.jirasoftwareBaseUrl != null && this.jirasoftwareUsername != null && this.jirasoftwarePassword != null;
    }

    public String getSonarqubeBaseUrl() {
        return sonarqubeBaseUrl;
    }
    public String getSonarqubeUsername() {
        return sonarqubeUsername;
    }
    public String getSonarqubePassword() {
        return sonarqubePassword;
    }

    public boolean isSonarqubeActive() {
        return this.sonarqubeBaseUrl != null && this.sonarqubeUsername != null && this.sonarqubePassword != null;
    }

    public String getElasicsearchBaseUrl() {
        return elasicsearchBaseUrl;
    }

    public boolean isElasticsearchActive() {
        return this.elasicsearchBaseUrl != null;
    }
}
