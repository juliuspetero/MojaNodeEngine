package com.mojagap.mojanode.infrastructure;

import com.mojagap.mojanode.infrastructure.utility.CommonUtil;
import com.mojagap.mojanode.infrastructure.utility.EnvironmentVariables;

public class ApplicationConstants {
    public static final String PLATFORM_TYPE_HEADER_KEY = "PLATFORM-TYPE";
    public static final String EMAIL_HEADER_KEY = "email";
    public static final String PASSWORD_HEADER_KEY = "password";
    public static final String BANK_TRANSFER_BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final String JWT_SECRET_KEY = CommonUtil.getEnvProperty(EnvironmentVariables.MOJA_NODE_JWT_SECRET_KEY, "q3t6w9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeTh");
    public static final String AUTHENTICATION_HEADER_NAME = "authentication";
    public static final String JWT_EXPIRATION_TIME_IN_MINUTES = CommonUtil.getEnvProperty(EnvironmentVariables.JWT_EXPIRATION_TIME_MINUTES, "30");
    public static final String DEFAULT_ROLE_NAME = "Super Administrator";
    public static final String DEFAULT_ROLE_DESCRIPTION = "This role provides all application permissions";
    public static final String DEFAULT_OFFICE_NAME = "Head Office";
    public static final String APP_USER_ID = "userId";
    public static final String CSV_CONTENT_TYPE = "text/csv";

}
