package at.grisa.agilemetrics.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CredentialManager {
    @Value("${:@null}")
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
