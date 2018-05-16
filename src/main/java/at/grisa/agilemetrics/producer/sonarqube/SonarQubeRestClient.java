package at.grisa.agilemetrics.producer.sonarqube;

import at.grisa.agilemetrics.producer.RestClient;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.sonarqube.restentity.*;
import at.grisa.agilemetrics.util.CredentialManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class SonarQubeRestClient {
    private static final Logger log = LogManager.getLogger(SonarQubeRestClient.class);

    public static final String PATH_LOGIN = "/api/authenticaton/login";
    public static final String PATH_COMPONENTS = "/api/projects/search";
    public static final String PATH_MEASURES = "/api/measures/component";
    private final RestClient restClient;

    public SonarQubeRestClient(CredentialManager credentialManager) {
        String host = credentialManager.getSonarqubeBaseUrl();
        String user = credentialManager.getSonarqubeUsername();
        String password = credentialManager.getSonarqubePassword();
        restClient = new RestClient(host, user, password);
    }

    public boolean checkConnection() {
        Integer statusCode = restClient.postReturnStatusNoAuthorization(PATH_LOGIN, new QueryParam("login", restClient.getUsername()), new QueryParam("password", restClient.getPassword()));
        log.info("SonarQube authentication test returns HTTP status " + statusCode);
        return statusCode == 200;
    }

    public Collection<Component> getComponents() {
        QueryParam qualifierQueryParam = new QueryParam("qualifiers", "TRK");
        Integer page = 1;
        Boolean nextPage = true;
        Component[] results = new Component[0];

        while (nextPage) {
            QueryParam pageQueryParam = new QueryParam("p", page);
            ComponentSearch componentSearch = restClient.getEntity(ComponentSearch.class, PATH_COMPONENTS, qualifierQueryParam, pageQueryParam);

            results = restClient.mergeArrays(results, componentSearch.getComponents(), Component.class);

            nextPage = componentSearch.getPaging().hasNextPage();
            page++;
        }

        return Arrays.asList(results);
    }

    public Collection<Measure> getMeasures(String componentKey, Metric... metrics) {
        QueryParam componentQueryParam = new QueryParam("component", componentKey);
        QueryParam metricsQueryParam = new QueryParam("metricKeys", Arrays.stream(metrics).map(Metric::toString).collect(Collectors.joining(",")));

        Collection<Measure> measures = null;
        ComponentMeasures componentMeasures = restClient.getEntity(ComponentMeasures.class, PATH_MEASURES, componentQueryParam, metricsQueryParam);

        if (componentMeasures.getComponent() != null) {
            measures = Arrays.asList(componentMeasures.getComponent().getMeasures());
        }

        return measures;
    }
}
