package at.grisa.agilemetrics.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PropertyManager {
    @Value("${producer.jirasoftware.workflow}")
    private String jirasoftwareWorkflow;
    @Value("${producer.jirasoftware.acceptanceCriteriaFieldName}")
    private String jirasoftwareAcceptanceCriteriaFieldName;

    public PropertyManager() {
    }

    public List<String> getJirasoftwareWorkflow() {
        if (jirasoftwareWorkflow != null) {
            return Arrays.asList(jirasoftwareWorkflow.split(","));
        } else {
            return null;
        }
    }

    public void setJirasoftwareWorkflow(String jirasoftwareWorkflow) {
        this.jirasoftwareWorkflow = jirasoftwareWorkflow;
    }

    public String getJirasoftwareAcceptanceCriteriaFieldName() {
        return jirasoftwareAcceptanceCriteriaFieldName;
    }

    public void setJirasoftwareAcceptanceCriteriaFieldName(String jirasoftwareAcceptanceCriteriaFieldName) {
        this.jirasoftwareAcceptanceCriteriaFieldName = jirasoftwareAcceptanceCriteriaFieldName;
    }
}
