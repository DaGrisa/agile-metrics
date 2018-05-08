package at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.greenhopper;

import at.grisa.agilemetrics.producer.jirasoftwareserver.restentity.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contents {
    private Value completedIssuesEstimateSum;
    private Value issuesNotCompletedEstimateSum;

    public Contents() {
    }

    public Value getCompletedIssuesEstimateSum() {
        return completedIssuesEstimateSum;
    }

    public void setCompletedIssuesEstimateSum(Value completedIssuesEstimateSum) {
        this.completedIssuesEstimateSum = completedIssuesEstimateSum;
    }

    public Value getIssuesNotCompletedEstimateSum() {
        return issuesNotCompletedEstimateSum;
    }

    public void setIssuesNotCompletedEstimateSum(Value issuesNotCompletedEstimateSum) {
        this.issuesNotCompletedEstimateSum = issuesNotCompletedEstimateSum;
    }
}
