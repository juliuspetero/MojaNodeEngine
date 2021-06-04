package com.mojagap.mojanode.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mojagap.mojanode.helper.ApplicationConstants;
import com.mojagap.mojanode.helper.utility.CommonUtils;
import com.mojagap.mojanode.helper.utility.DateUtils;
import com.mojagap.mojanode.model.ActionTypeEnum;
import com.mojagap.mojanode.model.EntityTypeEnum;
import com.mojagap.mojanode.model.http.HttpResponseStatusEnum;
import com.mojagap.mojanode.model.user.PlatformTypeEnum;
import com.mojagap.mojanode.model.user.UserActivityLog;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseController {

    private static final Logger LOG = Logger.getLogger(BaseController.class.getName());

    @Autowired
    protected HttpServletRequest httpServletRequest;

    protected UserActivityLog getUserActivityLog() {
        return (UserActivityLog) httpServletRequest.getAttribute(UserActivityLog.class.getName());
    }

    private void setUserActivityLogProperties(EntityTypeEnum entityTypeEnum, ActionTypeEnum actionTypeEnum, HttpResponseStatusEnum httpResponseStatusEnum, Throwable ex) {
        UserActivityLog userActivityLog = setUserActivityLogProperties(entityTypeEnum, actionTypeEnum, httpResponseStatusEnum);
        if (ex != null && userActivityLog != null) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            userActivityLog.setStackTrace(stackTrace);
        }
    }

    private UserActivityLog setUserActivityLogProperties(EntityTypeEnum entityTypeEnum, ActionTypeEnum actionTypeEnum, HttpResponseStatusEnum httpResponseStatusEnum) {
        UserActivityLog userActivityLog = getUserActivityLog();
        if (userActivityLog != null) {
            userActivityLog.setEntityType(entityTypeEnum);
            userActivityLog.setActionType(actionTypeEnum);
            userActivityLog.setResponseStatus(httpResponseStatusEnum);
        }
        return userActivityLog;
    }

    protected <R> R logUserActivity(EntityTypeEnum entityTypeEnum, ActionTypeEnum actionTypeEnum, Function<UserActivityLog, R> function) {
        UserActivityLog userActivityLog = setUserActivityLogProperties(entityTypeEnum, actionTypeEnum, HttpResponseStatusEnum.PENDING);
        try {
            R response = function.apply(userActivityLog);
            setUserActivityLogProperties(entityTypeEnum, actionTypeEnum, HttpResponseStatusEnum.SUCCESS, null);
            return response;
        } catch (Exception ex) {
            setUserActivityLogProperties(entityTypeEnum, actionTypeEnum, HttpResponseStatusEnum.FAILED, ex);
            LOG.log(Level.SEVERE, ex.getMessage() + " : " + getUserActivityLog(), ex);
            throw ex;
        }
    }

    protected <R> R executeHttpGet(Callable<R> callable) throws Exception {
        try {
            return callable.call();
        } catch (Exception ex) {
            logHttpGetUserActivity(ex);
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        }
    }

    private void logHttpGetUserActivity(Throwable ex) throws JsonProcessingException {
        UserActivityLog userActivityLog = getUserActivityLog();
        if (userActivityLog == null) {
            userActivityLog = new UserActivityLog();
        }
        Integer platformType = Integer.valueOf(httpServletRequest.getHeader(ApplicationConstants.PLATFORM_TYPE_HEADER_KEY));
        PlatformTypeEnum platformTypeEnum = PlatformTypeEnum.fromInt(platformType);

        String fullUrl = httpServletRequest.getRequestURI();
        String queryString = httpServletRequest.getQueryString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }
        userActivityLog.setRequestUrl(fullUrl);
        userActivityLog.setRequestMethod(httpServletRequest.getMethod());
        String headers = getRequestHeaders(httpServletRequest);
        userActivityLog.setRequestHeaders(headers);
        userActivityLog.setRemoteIpAddress(httpServletRequest.getRemoteAddr());
        userActivityLog.setCreatedOn(DateUtils.now());
        userActivityLog.setPlatformType(platformTypeEnum.getId());
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        userActivityLog.setStackTrace(stackTrace);
        userActivityLog.setResponseStatus(HttpResponseStatusEnum.FAILED);
        httpServletRequest.setAttribute(UserActivityLog.class.getName(), userActivityLog);
    }

    private String getRequestHeaders(HttpServletRequest httpServletRequest) throws JsonProcessingException {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            headerMap.put(key, value);
        }
        return CommonUtils.OBJECT_MAPPER.writeValueAsString(headerMap);
    }


}