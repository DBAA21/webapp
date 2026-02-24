package com.csye6225.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Bean
    @ConditionalOnProperty(name = "aws.s3.bucket-name", havingValue = "", matchIfMissing = true)
    public S3Client noOpS3Client() {
        // Return null when bucket name is empty or missing (local dev / tests)
        return null;
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.bucket-name", matchIfMissing = false)
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
