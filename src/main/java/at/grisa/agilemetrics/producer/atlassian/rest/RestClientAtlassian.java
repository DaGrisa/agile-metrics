package at.grisa.agilemetrics.producer.atlassian.rest;

import at.grisa.agilemetrics.producer.RestClient;
import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import at.grisa.agilemetrics.producer.bitbucketserver.BitBucketServerRestClient;
import at.grisa.agilemetrics.producer.bitbucketserver.restentity.PagedEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;

public class RestClientAtlassian extends RestClient {
    private static final Logger log = LogManager.getLogger(BitBucketServerRestClient.class);

    public RestClientAtlassian(String hostUrl, String user, String password) {
        super(hostUrl, user, password);
    }

    @Override
    public <T> T getEntity(Class<T> clazz, String restPath, QueryParam... queryParams) {
        String restUrl = this.getHostUrl() + restPath;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(this.getUsername(), this.getPassword()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl);

        for (QueryParam queryParam : queryParams) {
            builder.queryParam(queryParam.name, queryParam.value);
        }

        return restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                null,
                clazz
        ).getBody();
    }

    public <T> T getPagedEntities(ParameterizedTypeReference<T> responseType, String restPath,
                                  QueryParam... queryParams) {
        String restUrl = this.getHostUrl() + restPath;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(this.getUsername(), this.getPassword()));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl);

        for (QueryParam queryParam : queryParams) {
            builder.queryParam(queryParam.name, queryParam.value);
        }

        try {
            return restTemplate.exchange(builder.build().encode().toUri(),
                    HttpMethod.GET,
                    null,
                    responseType
            ).getBody();
        } catch (HttpClientErrorException e) {
            log.error("GET request to resource " + restUrl + " failed");
            throw new RuntimeException("Error retrieving paged entities", e);
        }
    }

    public <T> T[] getAllEntities(Class<T> clazz, ParameterizedTypeReference<PagedEntities<T>> responseType, String restPath,
                                  QueryParam... queryParams) {
        String restUrl = this.getHostUrl() + restPath;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(this.getUsername(), this.getPassword()));

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
                entities = this.mergeArrays(entities, response.getValues(), clazz);
                startElement = response.getNextPageStart();
            } else {
                break;
            }
        } while (!response.getIsLastPage());

        return entities;
    }
}
