package com.mojagap.mojanode.infrastructure.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.mojagap.mojanode.infrastructure.EnvironmentVariable;
import com.mojagap.mojanode.infrastructure.utility.Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfiguration {

    @Bean
    public AmazonS3 s3() {
        String accessKey = Util.getEnvProperty(EnvironmentVariable.AWS_MOJA_NODE_ACCESS_KEY.getValue());
        String secretKey = Util.getEnvProperty(EnvironmentVariable.AWS_MOJA_NODE_SECRET_KEY.getValue());
        String region = Util.getEnvProperty(EnvironmentVariable.AWS_MOJA_NODE_REGION.getValue());
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
