package com.mojagap.mojanode.service.httpgateway;

import com.mojagap.mojanode.helper.filter.HttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class RestTemplateService {

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setInterceptors(Collections.singletonList(getHttpRequestInterceptor()));
        return restTemplate;
    }

    @Bean
    public HttpRequestInterceptor getHttpRequestInterceptor() {
        return new HttpRequestInterceptor();
    }


    //    @SneakyThrows(JsonProcessingException.class)
    public <R> R makeApiCall(HttpMethod httpMethod, String path, Map<String, String> queryParameter, MultiValueMap<String, String> headers, Object body, Class<R> responseType) {
        return null;
    }

    public <R> R doHttpGet(String url, Map<String, String> queryParams, Class<R> responseType) {
        return restTemplate.getForObject(url, responseType, queryParams);
    }

    public <R> R doHttpPost(String path, Object body, Class<R> responseType) {
        return restTemplate.postForObject(path, body, responseType);
    }
}
