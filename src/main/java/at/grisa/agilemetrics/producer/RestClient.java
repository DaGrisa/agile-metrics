package at.grisa.agilemetrics.producer;

import at.grisa.agilemetrics.producer.atlassian.rest.entities.QueryParam;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;

public class RestClient {
    private final String hostUrl;
    private final String username;
    private final String password;

    private HttpComponentsClientHttpRequestFactory httpRequestFactory;

    public RestClient(String hostUrl, String user, String password) {
        this.hostUrl = hostUrl;
        this.username = user;
        this.password = password;

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        HttpClient httpClient = clientBuilder.build();
        httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient);
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

    public void setHttpProxy(String host, Integer port) {
        HttpHost myProxy = new HttpHost(host, port);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setProxy(myProxy);
        HttpClient httpClient = clientBuilder.build();
        httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient);
    }

    public void setHttpProxyAuth(String host, Integer port, String username, String password) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));
        HttpHost myProxy = new HttpHost(host, port);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider);
        HttpClient httpClient = clientBuilder.build();
        httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient);
    }

    public <T> T getEntity(Class<T> clazz, String restPath, QueryParam... queryParams) {
        String restUrl = this.hostUrl + restPath;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));

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

    public Integer postReturnStatusNoAuthorization(String restPath, QueryParam... queryParams) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);

        String restUrl = this.hostUrl + restPath;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restUrl);

        for (QueryParam queryParam : queryParams) {
            builder.queryParam(queryParam.name, queryParam.value);
        }

        return restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.POST,
                null,
                String.class
        ).getStatusCodeValue();
    }
}
