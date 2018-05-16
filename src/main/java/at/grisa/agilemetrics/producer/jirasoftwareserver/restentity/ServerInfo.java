package at.grisa.agilemetrics.producer.jirasoftwareserver.restentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInfo {
    private String baseUrl;
    private String version;
    private String buildNumber;
    private String scmInfo;
    private String buildPartnerName;
    private String serverTitle;

    public ServerInfo() {
        // default constructor
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getScmInfo() {
        return scmInfo;
    }

    public void setScmInfo(String scmInfo) {
        this.scmInfo = scmInfo;
    }

    public String getBuildPartnerName() {
        return buildPartnerName;
    }

    public void setBuildPartnerName(String buildPartnerName) {
        this.buildPartnerName = buildPartnerName;
    }

    public String getServerTitle() {
        return serverTitle;
    }

    public void setServerTitle(String serverTitle) {
        this.serverTitle = serverTitle;
    }

    public boolean isEmpty() {
        return baseUrl == null && version == null && buildNumber == null &&
                scmInfo == null && buildPartnerName == null && serverTitle == null;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "baseUrl='" + baseUrl + '\'' +
                ", version='" + version + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                ", scmInfo='" + scmInfo + '\'' +
                ", buildPartnerName='" + buildPartnerName + '\'' +
                ", serverTitle='" + serverTitle + '\'' +
                '}';
    }
}
