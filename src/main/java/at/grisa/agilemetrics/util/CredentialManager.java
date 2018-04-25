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
}
