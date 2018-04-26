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

    public void setBitbucketserverBaseUrl(String bitbucketserverBaseUrl) {
        this.bitbucketserverBaseUrl = bitbucketserverBaseUrl;
    }

    public String getBitbucketserverUsername() {
        return bitbucketserverUsername;
    }

    public void setBitbucketserverUsername(String bitbucketserverUsername) {
        this.bitbucketserverUsername = bitbucketserverUsername;
    }

    public String getBitbucketserverPassword() {
        return bitbucketserverPassword;
    }

    public void setBitbucketserverPassword(String bitbucketserverPassword) {
        this.bitbucketserverPassword = bitbucketserverPassword;
    }

    public boolean isBitbucketserverActive() {
        return !this.bitbucketserverBaseUrl.isEmpty() && !this.bitbucketserverUsername.isEmpty() && !this.bitbucketserverPassword.isEmpty();
    }

    public String getJirasoftwareBaseUrl() {
        return jirasoftwareBaseUrl;
    }

    public void setJirasoftwareBaseUrl(String jirasoftwareBaseUrl) {
        this.jirasoftwareBaseUrl = jirasoftwareBaseUrl;
    }

    public String getJirasoftwareUsername() {
        return jirasoftwareUsername;
    }

    public void setJirasoftwareUsername(String jirasoftwareUsername) {
        this.jirasoftwareUsername = jirasoftwareUsername;
    }

    public String getJirasoftwarePassword() {
        return jirasoftwarePassword;
    }

    public void setJirasoftwarePassword(String jirasoftwarePassword) {
        this.jirasoftwarePassword = jirasoftwarePassword;
    }

    public String getSonarqubeBaseUrl() {
        return sonarqubeBaseUrl;
    }

    public void setSonarqubeBaseUrl(String sonarqubeBaseUrl) {
        this.sonarqubeBaseUrl = sonarqubeBaseUrl;
    }

    public String getSonarqubeUsername() {
        return sonarqubeUsername;
    }

    public void setSonarqubeUsername(String sonarqubeUsername) {
        this.sonarqubeUsername = sonarqubeUsername;
    }

    public String getSonarqubePassword() {
        return sonarqubePassword;
    }

    public void setSonarqubePassword(String sonarqubePassword) {
        this.sonarqubePassword = sonarqubePassword;
    }
}
