package com.mojagap.mojanode.infrastructure.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.mojagap.mojanode.infrastructure.EnvironmentVariable;
import com.mojagap.mojanode.model.common.Environment;
import org.springframework.beans.BeanUtils;

public class Util {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

    public static String getEnvProperty(String property, String defaultValue) {
        String sysProperty = System.getProperty(property);
        if (sysProperty == null) {
            sysProperty = System.getenv(property);
        }
        if (sysProperty == null) {
            sysProperty = defaultValue;
        }
        return sysProperty;
    }

    public static String getEnvProperty(String property) {
        String sysProperty = System.getProperty(property);
        if (sysProperty == null) {
            sysProperty = System.getenv(property);
        }
        return sysProperty;
    }

    public static String getRootProjectDirectory() {
        return System.getProperty("user.dir");
    }

    public static <R> R copyProperties(Object source, R target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static boolean isDevelopmentEnv() {
        return Util.getEnvProperty(EnvironmentVariable.ENVIRONMENT.getValue()).equals(Environment.DEVELOPMENT.name());
    }

    public static boolean isTestEnv() {
        return Util.getEnvProperty(EnvironmentVariable.ENVIRONMENT.getValue()).equals(Environment.TEST.name());
    }

    public static boolean isProductionEnv() {
        return Util.getEnvProperty(EnvironmentVariable.ENVIRONMENT.getValue()).equals(Environment.PRODUCTION.name());
    }

}
