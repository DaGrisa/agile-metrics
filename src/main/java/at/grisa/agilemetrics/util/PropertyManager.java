package at.grisa.agilemetrics.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class PropertyManager {
    @Value("${producer.jirasoftware.workflow:@null}")
    private String jirasoftwareWorkflow;
    @Value("${producer.jirasoftware.acceptanceCriteriaFieldName:@null}")
    private String jirasoftwareAcceptanceCriteriaFieldName;

    @Value("${producer.sonarqube.metrics:@null}")
    private String sonarqubeMetrics;

    @Value("${consumer.elasticsearch.batchSize:@null}")
    private String elasticsearchBatchSize;
    private static final Integer ELASTICSEARCH_BATCHSIZE_DEFAULT = 50;

    @Value("${consumer.elasticsearch.indexName:@null}")
    private String elasticsearchIndexName;

    @Value("${consumer.elasticsearch.typeName:@null}")
    private String elasticsearchTypeName;

    public PropertyManager() {
        // default constructor
    }

    public List<String> getJirasoftwareWorkflow() {
        if (jirasoftwareWorkflow != null) {
            return Arrays.asList(jirasoftwareWorkflow.split(","));
        } else {
            return new LinkedList<>();
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

    public String getSonarqubeMetrics() {
        return sonarqubeMetrics;
    }

    public void setSonarqubeMetrics(String sonarqubeMetrics) {
        this.sonarqubeMetrics = sonarqubeMetrics;
    }

    public Integer getElasticSearchBatchSize() {
        if (elasticsearchBatchSize == null || elasticsearchBatchSize.isEmpty()) {
            return ELASTICSEARCH_BATCHSIZE_DEFAULT;
        }
        return Integer.valueOf(elasticsearchBatchSize);
    }

    public void setElasticsearchBatchSize(String elasticsearchBatchSize) {
        this.elasticsearchBatchSize = elasticsearchBatchSize;
    }

    public String getElasticSearchIndexName() {
        return this.elasticsearchIndexName;
    }

    public void setElasticsearchIndexName(String elasticsearchIndexName) {
        this.elasticsearchIndexName = elasticsearchIndexName;
    }

    public String getElasticSearchTypeName() {
        return this.elasticsearchTypeName;
    }

    public void setElasticsearchTypeName(String elasticsearchTypeName) {
        this.elasticsearchTypeName = elasticsearchTypeName;
    }
}
