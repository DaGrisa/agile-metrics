package at.grisa.agilemetrics.producer.atlassian.rest;

import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.bitbucketserver.restentities.PagedEntities;
import org.springframework.core.ParameterizedTypeReference;
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

    public <T> T getPagedEntities(ParameterizedTypeReference<T> responseType, String restPath,
                                  QueryParam... queryParams) {
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
                responseType
        ).getBody();

        return response;
    }

    public <T> T[] getAllEntities(Class<T> clazz, ParameterizedTypeReference<PagedEntities<T>> responseType, String restPath,
                                  QueryParam... queryParams) {
        String restUrl = this.hostUrl + restPath;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

        @SuppressWarnings("unchecked")
        T[] entities = (T[]) Array.newInstance(clazz, 0);
        PagedEntities<T> response;
        Integer startElement = 0;

        do {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl)
                    .queryParam("start", startElement);

            for (QueryParam queryParam : queryParams) {
                builder.queryParam(queryParam.name, queryParam.value);
            }

            response = restTemplate.exchange(builder.build().encode().toUri(),
                    HttpMethod.GET,
                    null,
                    responseType
            ).getBody();

            if (response != null) {
                entities = mergeArrays(entities, response.getValues(), clazz);
                startElement = response.getNextPageStart();
            } else {
                break;
            }
        } while (!response.getIsLastPage());

        return entities;
    }

    private <T> T[] mergeArrays(T[] entities, T[] responseEntities, Class<T> clazz) {
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
