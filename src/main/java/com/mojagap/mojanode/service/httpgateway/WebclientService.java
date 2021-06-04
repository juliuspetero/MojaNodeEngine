package com.mojagap.mojanode.service.httpgateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mojagap.mojanode.helper.ApplicationConstants;
import com.mojagap.mojanode.helper.utility.CommonUtils;
import com.mojagap.mojanode.helper.utility.DateUtils;
import com.mojagap.mojanode.model.ActionTypeEnum;
import com.mojagap.mojanode.model.http.ExternalUser;
import com.mojagap.mojanode.model.http.HttpCallLog;
import com.mojagap.mojanode.repository.http.HttpCallLogRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebclientService {

    private final Logger LOG = Logger.getLogger(WebclientService.class.getName());

    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(logHttpRequest())
            .filter(logHttpResponse())
            .build();

    private HttpCallLog httpCallLog;

    @Autowired
    public HttpCallLogRepository httpCallLogRepository;


    public ExternalUser getExternalUserById(Integer id) {
        MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>();
        queryParameters.add("name", "John");
        queryParameters.add("age", "45");
        queryParameters.add("sex", "MALE");

        MultiValueMap<String, String> httpHeaders = new LinkedMultiValueMap<>();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.DATE, DateUtils.now().toString());
        String response = makeApiCall(HttpMethod.GET, ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users/" + id, queryParameters, httpHeaders, new ExternalUser(), String.class);
        return new ExternalUser();
    }

    public List<ExternalUser> getAllExternalUsers() {
        return List.of(makeApiCall(HttpMethod.POST, ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users", null, null, null, ExternalUser[].class));
    }

    public ExternalUser createExternalUser(ExternalUser externalUser) {
        MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>();
        queryParameters.add("name", "John");
        queryParameters.add("age", "45");
        queryParameters.add("sex", "MALE");

        MultiValueMap<String, String> httpHeaders = new LinkedMultiValueMap<>();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.DATE, DateUtils.now().toString());

        ExternalUser response = makeApiCall(HttpMethod.POST, ApplicationConstants.BANK_TRANSFER_BASE_URL + "/users", queryParameters, httpHeaders, externalUser, ExternalUser.class);

        return response;
    }

    @SneakyThrows(JsonProcessingException.class)
    public <R> R makeApiCall(HttpMethod httpMethod, String path, MultiValueMap<String, String> queryParameter, MultiValueMap<String, String> headers, Object body, Class<R> responseType) {
        Long startTime = System.currentTimeMillis();
        httpCallLog = new HttpCallLog();
        httpCallLog.setActionType(ActionTypeEnum.MONEY_TRANSFER);
        httpCallLog.setCreatedOn(DateUtils.now());
        String requestBody = CommonUtils.OBJECT_MAPPER.writeValueAsString(body);
        httpCallLog.setRequestBody(requestBody);
        R responseBody = webClient.method(httpMethod)
                .uri(path, uriBuilder -> uriBuilder.queryParams(queryParameter).build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(Mono.just(body), Object.class)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(responseType))
                .block();
        Long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        httpCallLog.setDuration((int) duration);
        httpCallLogRepository.saveAndFlush(httpCallLog);
        return responseBody;
    }

    private ExchangeFilterFunction logHttpRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String url = clientRequest.url().toString();
            httpCallLog.setRequestUrl(url);
            try {
                httpCallLog.setRequestHeaders(CommonUtils.OBJECT_MAPPER.writeValueAsString(clientRequest.headers()));
            } catch (JsonProcessingException ex) {
                LOG.log(Level.WARNING, "Failed to convert to JSON string : " + ex.getMessage(), ex);
            }
            httpCallLog.setRequestMethod(clientRequest.method().name());
            LOG.log(Level.INFO, "Making Http Request to : " + clientRequest.url().getPath(), clientRequest);
            return Mono.just(clientRequest);
        });
    }


    private ExchangeFilterFunction logHttpResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            try {
                HttpHeaders responseHeaders = clientResponse.headers().asHttpHeaders();
                httpCallLog.setResponseHeaders(CommonUtils.OBJECT_MAPPER.writeValueAsString(responseHeaders));
            } catch (JsonProcessingException ex) {
                LOG.log(Level.WARNING, "Failed to convert response headers to JSON string : " + ex.getMessage(), ex);
            }
            httpCallLog.setResponseStatusCode(clientResponse.rawStatusCode());
            return Mono.just(clientResponse);
        });
    }



}
