package at.grisa.agilemetrics.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CredentialManager {
    @Value("${producer.bitbucketserver.baseUrl}")
    private String bitbucketserverBaseUrl;
    @Value("${producer.bitbucketserver.username}")
    private String bitbucketserverUsername;
    @Value("${producer.bitbucketserver.password}")
    private String bitbucketserverPassword;

    @Value("${producer.jirasoftware.baseUrl}")
    private String jirasoftwareBaseUrl;
    @Value("${producer.jirasoftware.username}")
    private String jirasoftwareUsername;
    @Value("${producer.jirasoftware.password}")
    private String jirasoftwarePassword;

    @Value("${producer.sonarqube.baseUrl}")
    private String sonarqubeBaseUrl;
    @Value("${producer.sonarqube.username}")
    private String sonarqubeUsername;
    @Value("${producer.sonarqube.password}")
    private String sonarqubePassword;

    public CredentialManager() {
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
        return !this.bitbucketserverBaseUrl.isEmpty() && !this.bitbucketserverUsername.isEmpty() && !this.bitbucketserverPassword.isEmpty();
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
        return !this.jirasoftwareBaseUrl.isEmpty() && !this.jirasoftwareUsername.isEmpty() && !this.jirasoftwarePassword.isEmpty();
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
        return !this.sonarqubeBaseUrl.isEmpty() && !this.sonarqubeUsername.isEmpty() && !this.sonarqubePassword.isEmpty();
    }
}
