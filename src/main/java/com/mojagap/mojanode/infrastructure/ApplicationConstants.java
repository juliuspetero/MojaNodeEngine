package com.mojagap.mojanode.infrastructure;

import com.mojagap.mojanode.infrastructure.utility.CommonUtils;
import com.mojagap.mojanode.infrastructure.utility.EnvironmentVariables;

public class ApplicationConstants {
    public static final String DD_MMM_YYY = "dd/MMM/yyyy";
    public static final String PLATFORM_TYPE_HEADER_KEY = "PLATFORM-TYPE";
    public static final String BANK_TRANSFER_BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final String CREATE_USER_URL = "/user/create";
    public static final String CREATE_ORGANIZATION_URL = "/organization/create";
    public static final String AUTHENTICATION_URL = "/user/authenticate";
    public static final String JWT_SECRET_KEY = CommonUtils.getEnvProperty(EnvironmentVariables.MOJA_NODE_JWT_SECRET_KEY, "q3t6w9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeTh");
    public static final String AUTHENTICATION_HEADER_NAME = "authentication";
    public static final Long JWT_EXPIRATION_TIME = Long.valueOf(CommonUtils.getEnvProperty(EnvironmentVariables.JWT_EXPIRATION_TIME, "" + 1000L * 60 * 60));
}
