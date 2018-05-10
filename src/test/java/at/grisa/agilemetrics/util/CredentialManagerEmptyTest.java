package at.grisa.agilemetrics.util;

import at.grisa.agilemetrics.ApplicationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CredentialManager.class, ApplicationConfig.class})
@TestPropertySource("classpath:credential-manager-empty-test.properties")
public class CredentialManagerEmptyTest {
    @Autowired
    CredentialManager credentialManager;

    @Test
    public void checkValuesNull() {
        assertNull("bitbucket server baseurl is null", credentialManager.getBitbucketserverBaseUrl());
        assertNull("bitbucket server username is null", credentialManager.getBitbucketserverUsername());
        assertNull("bitbucket server password is null", credentialManager.getBitbucketserverPassword());
        assertFalse("bitbucket is not active", credentialManager.isBitbucketserverActive());

        assertNull("jira software baseurl is null", credentialManager.getJirasoftwareBaseUrl());
        assertNull("jira software username is null", credentialManager.getJirasoftwareUsername());
        assertNull("jira software password is null", credentialManager.getJirasoftwarePassword());
        assertFalse("jira software is not active", credentialManager.isJirasoftwareActive());

        assertNull("sonarqube baseurl is null", credentialManager.getSonarqubeBaseUrl());
        assertNull("sonarqube username is null", credentialManager.getSonarqubeUsername());
        assertNull("sonarqube password is null", credentialManager.getSonarqubePassword());
        assertFalse("sonarqube is not active", credentialManager.isSonarqubeActive());

        assertNull("elasticsearch baseurl is null", credentialManager.getElasicsearchBaseUrl());
        assertFalse("elasticsearch is not active", credentialManager.isElasticsearchActive());
    }
}
