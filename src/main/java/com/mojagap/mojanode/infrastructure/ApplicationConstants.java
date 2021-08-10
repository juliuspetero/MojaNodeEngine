package com.mojagap.mojanode.infrastructure;

import com.mojagap.mojanode.infrastructure.utility.Util;

public class ApplicationConstants {
    public static final String PLATFORM_TYPE_HEADER_KEY = "PLATFORM-TYPE";
    public static final String EMAIL_HEADER_KEY = "email";
    public static final String PASSWORD_HEADER_KEY = "password";
    public static final String BANK_TRANSFER_BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final String JWT_SECRET_KEY = Util.getEnvProperty(EnvironmentVariable.MOJA_NODE_JWT_SECRET_KEY.getValue(), "q3t6w9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeTh");
    public static final String AUTHENTICATION_HEADER_NAME = "authentication";
    public static final String JWT_EXPIRATION_TIME_IN_MINUTES = Util.getEnvProperty(EnvironmentVariable.JWT_EXPIRATION_TIME_MINUTES.getValue(), "30");
    public static final String DEFAULT_ROLE_NAME = "Super Administrator";
    public static final String DEFAULT_ROLE_DESCRIPTION = "This role provides all application permissions";
    public static final String DEFAULT_OFFICE_NAME = "Head Office";
    public static final String APP_USER_ID = "userId";
    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

}
