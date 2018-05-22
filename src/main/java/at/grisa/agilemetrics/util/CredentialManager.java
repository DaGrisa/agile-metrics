package at.grisa.agilemetrics.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CredentialManager {
    private static final Logger log = LogManager.getLogger(CredentialManager.class);

    @Value("${httpProxy.host:@null}")
    private String httpProxyHost;
    @Value("${httpProxy.port:@null}")
    private String httpProxyPort;
    @Value("${httpProxy.user:@null}")
    private String httpProxyUser;
    @Value("${httpProxy.password:@null}")
    private String httpProxyPassword;

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

    public String getHttpProxyHost() {
        return httpProxyHost;
    }

    public Integer getHttpProxyPort() {
        int portNr = -1;
        try {
            portNr = Integer.parseInt(httpProxyPort);
        } catch (NumberFormatException e) {
            log.error("Unable to parse http proxy port as integer.");
        }
        return portNr;
    }

    public String getHttpProxyUser() {
        return httpProxyUser;
    }

    public String getHttpProxyPassword() {
        return httpProxyPassword;
    }

    public boolean isProxyActive() {
        return httpProxyHost != null && httpProxyPort != null;
    }

    public boolean isProxyAuthActive() {
        return isProxyActive() && httpProxyUser != null && httpProxyPassword != null;
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
