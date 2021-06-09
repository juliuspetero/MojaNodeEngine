package com.mojagap.mojanode.infrastructure.logger;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.utility.CommonUtils;
import com.mojagap.mojanode.infrastructure.utility.DateUtils;
import com.mojagap.mojanode.model.user.PlatformTypeEnum;
import com.mojagap.mojanode.model.user.UserActivityLog;
import com.mojagap.mojanode.repository.user.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserActivityLogFilter implements Filter {

    @Autowired
    private UserActivityLogRepository userActivityLogRepository;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    private static final Logger LOG = Logger.getLogger(UserActivityLogFilter.class.getName());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            long startTime = System.currentTimeMillis();
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

            Integer platformType = Integer.valueOf(requestWrapper.getHeader(ApplicationConstants.PLATFORM_TYPE_HEADER_KEY));
            PlatformTypeEnum platformTypeEnum = PlatformTypeEnum.fromInt(platformType);

            if (!requestWrapper.getMethod().equals(HttpMethod.GET.name())) {
                UserActivityLog userActivityLog;
                userActivityLog = initializeUserActivityLog(requestWrapper);
                userActivityLog.setPlatformType(platformTypeEnum.getId());
                requestWrapper.setAttribute(UserActivityLog.class.getName(), userActivityLog);
            }

            filterChain.doFilter(requestWrapper, responseWrapper);

            UserActivityLog userActivityLog = (UserActivityLog) requestWrapper.getAttribute(UserActivityLog.class.getName());
            if (userActivityLog != null) {
                String requestBody = new String(requestWrapper.getContentAsByteArray());
                String responseBody = new String(responseWrapper.getContentAsByteArray());
                userActivityLog.setRequestBody(requestBody);
                userActivityLog.setResponseBody(responseBody);
                responseWrapper.copyBodyToResponse();
                setHttpResponseProperties(responseWrapper, userActivityLog);

                long endTime = System.currentTimeMillis();
                userActivityLog.setDuration((int) (endTime - startTime));
                userActivityLogRepository.saveAndFlush(userActivityLog);
            } else {
                responseWrapper.copyBodyToResponse();
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Failure Logging User Activities : ", ex);
            handlerExceptionResolver.resolveException((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, null, ex);
        }
    }

    private UserActivityLog initializeUserActivityLog(ContentCachingRequestWrapper requestWrapper) throws IOException {
        UserActivityLog userActivityLog = new UserActivityLog();
        String fullUrl = requestWrapper.getRequestURI();
        String queryString = requestWrapper.getQueryString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }
        userActivityLog.setRequestUrl(fullUrl);
        userActivityLog.setRequestMethod(requestWrapper.getMethod());
        String headers = getRequestHeaders(requestWrapper);
        userActivityLog.setRequestHeaders(headers);
        userActivityLog.setRemoteIpAddress(requestWrapper.getRemoteAddr());
        userActivityLog.setCreatedOn(DateUtils.now());
        return userActivityLog;
    }

    private void setHttpResponseProperties(ContentCachingResponseWrapper responseWrapper, UserActivityLog userActivityLog) throws JsonProcessingException {
        String responseHeaders = getResponseHeaders(responseWrapper);
        userActivityLog.setResponseHeaders(responseHeaders);
        userActivityLog.setResponseStatusCode(responseWrapper.getStatus());
    }


    private String getRequestHeaders(ContentCachingRequestWrapper requestWrapper) throws JsonProcessingException {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = requestWrapper.getHeader(key);
            headerMap.put(key, value);
        }
        return CommonUtils.OBJECT_MAPPER.writeValueAsString(headerMap);
    }

    private String getResponseHeaders(ContentCachingResponseWrapper responseWrapper) throws JsonProcessingException {
        Map<String, String> headerMap = new HashMap<>();
        Collection<String> headerNames = responseWrapper.getHeaderNames();
        for (String key : headerNames) {
            String value = responseWrapper.getHeader(key);
            headerMap.put(key, value);
        }
        return CommonUtils.OBJECT_MAPPER.writeValueAsString(headerMap);
    }


}
