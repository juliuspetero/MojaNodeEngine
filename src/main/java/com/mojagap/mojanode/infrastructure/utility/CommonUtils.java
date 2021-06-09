package com.mojagap.mojanode.infrastructure.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
}
