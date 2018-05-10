package at.grisa.agilemetrics.util;

import at.grisa.agilemetrics.ApplicationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CredentialManager.class, ApplicationConfig.class})
@TestPropertySource("classpath:credential-manager-test.properties")
public class CredentialManagerTest {
    @Autowired
    CredentialManager credentialManager;

    @Test
    public void checkValuesNull() {
        assertNotNull("bitbucket server baseurl is not null", credentialManager.getBitbucketserverBaseUrl());
        assertNotNull("bitbucket server username is not null", credentialManager.getBitbucketserverUsername());
        assertNotNull("bitbucket server password is not null", credentialManager.getBitbucketserverPassword());
        assertTrue("bitbucket is active", credentialManager.isBitbucketserverActive());

        assertNotNull("jira software baseurl is not null", credentialManager.getJirasoftwareBaseUrl());
        assertNotNull("jira software username is not null", credentialManager.getJirasoftwareUsername());
        assertNotNull("jira software password is not null", credentialManager.getJirasoftwarePassword());
        assertTrue("jira software is active", credentialManager.isJirasoftwareActive());

        assertNotNull("sonarqube baseurl is not null", credentialManager.getSonarqubeBaseUrl());
        assertNotNull("sonarqube username is not null", credentialManager.getSonarqubeUsername());
        assertNotNull("sonarqube password is not null", credentialManager.getSonarqubePassword());
        assertTrue("sonarqube is active", credentialManager.isSonarqubeActive());

        assertNotNull("elasticsearch baseurl is not null", credentialManager.getElasicsearchBaseUrl());
        assertTrue("elasticsearch is active", credentialManager.isElasticsearchActive());
    }
}
