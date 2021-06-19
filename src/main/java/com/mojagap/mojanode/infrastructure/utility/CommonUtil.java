package com.mojagap.mojanode.infrastructure.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class CommonUtil {

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

    public static String getRootProjectDirectory() {
        return System.getProperty("user.dir");
    }

}
