package at.grisa.agilemetrics.producer;

import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;

public class RestClient {
    private final String hostUrl;
    private final String username;
    private final String password;

    public RestClient(String hostUrl, String user, String password) {
        this.hostUrl = hostUrl;
        this.username = user;
        this.password = password;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public <T> T getEntity(Class<T> clazz, String restPath, QueryParam... queryParams) {
        String restUrl = this.hostUrl + restPath;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl);

        for (QueryParam queryParam : queryParams) {
            builder.queryParam(queryParam.name, queryParam.value);
        }

        T response = restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                null,
                clazz
        ).getBody();

        return response;
    }

    public <T> T[] mergeArrays(T[] entities, T[] responseEntities, Class<T> clazz) {
        int totalLength = entities.length + responseEntities.length;

        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz, totalLength);

        int index = 0;
        for (T entity : entities) {
            result[index++] = entity;
        }
        for (T entity : responseEntities) {
            result[index++] = entity;
        }

        return result;
    }
}
