package com.csye6225.webapp.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CloudPlatformDetector {

    private static final Logger logger = LoggerFactory.getLogger(CloudPlatformDetector.class);

    private static final String AWS_BASE = "http://169.254.169.254";
    private static final String GCP_BASE = "http://metadata.google.internal/computeMetadata/v1/";

    private final HttpClient httpClient;
    private volatile String cachedPlatform;

    public CloudPlatformDetector() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public String detect() {
        if (cachedPlatform != null) {
            return cachedPlatform;
        }

        if (isGcp()) {
            cachedPlatform = "gcp";
            return cachedPlatform;
        }

        if (isAws()) {
            cachedPlatform = "aws";
            return cachedPlatform;
        }

        return null;
    }

    private boolean isGcp() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GCP_BASE + "instance/id"))
                .timeout(Duration.ofSeconds(2))
                .header("Metadata-Flavor", "Google")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException ex) {
            logger.debug("GCP metadata probe failed: {}", ex.getMessage());
            return false;
        }
    }

    private boolean isAws() {
        try {
            String token = fetchAwsToken();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AWS_BASE + "/latest/meta-data/instance-id"))
                    .timeout(Duration.ofSeconds(2))
                    .header("X-aws-ec2-metadata-token", token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException ex) {
            logger.debug("AWS metadata probe failed: {}", ex.getMessage());
            return false;
        }
    }

    private String fetchAwsToken() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AWS_BASE + "/latest/api/token"))
                .timeout(Duration.ofSeconds(2))
                .header("X-aws-ec2-metadata-token-ttl-seconds", "60")
                .method("PUT", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.warn("IMDSv2 token request failed: status={} body=\"{}\"", response.statusCode(), response.body());
            throw new IOException("Failed to fetch IMDSv2 token: " + response.statusCode());
        }
        return response.body().trim();
    }
}
