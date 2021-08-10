package com.mojagap.mojanode.infrastructure.utility;

import com.mojagap.mojanode.infrastructure.EnvironmentVariable;

public class AwsUtil {
    private static String s3BucketName;

    public static String getS3BucketName() {
        if (s3BucketName != null) {
            return s3BucketName;
        } else if (Util.isDevelopmentEnv()) {
            s3BucketName = Util.getEnvProperty(EnvironmentVariable.AWS_DEVELOPMENT_S3_BUCKET.getValue());
        } else if (Util.isTestEnv()) {
            s3BucketName = Util.getEnvProperty(EnvironmentVariable.AWS_TEST_S3_BUCKET.getValue());
        } else if (Util.isProductionEnv()) {
            s3BucketName = Util.getEnvProperty(EnvironmentVariable.AWS_PRODUCTION_S3_BUCKET.getValue());
        }
        return s3BucketName;
    }

    public static boolean isAwsEnabled() {
        return Util.getEnvProperty(EnvironmentVariable.AWS_ENABLED.getValue()).equals("true");
    }
}
