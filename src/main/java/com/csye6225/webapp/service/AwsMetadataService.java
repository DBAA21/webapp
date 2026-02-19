package com.csye6225.webapp.service;

import com.csye6225.webapp.dto.MetadataResponse;
import com.csye6225.webapp.dto.NetworkInterfaceDto;
import com.csye6225.webapp.exception.MetadataUnavailableException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AwsMetadataService implements MetadataService {

    private static final Logger logger = LoggerFactory.getLogger(AwsMetadataService.class);

    private static final String AWS_BASE = "http://169.254.169.254";

    private final HttpClient httpClient;

    public AwsMetadataService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Override
    public MetadataResponse getMetadata() {
        try {
            String token = fetchToken();
            String instanceId = getMetadataValue("/latest/meta-data/instance-id", token);
            String availabilityZone = getMetadataValue("/latest/meta-data/placement/availability-zone", token);
            String machineType = getMetadataValue("/latest/meta-data/instance-type", token);
            String region = toRegion(availabilityZone);

            List<NetworkInterfaceDto> interfaces = fetchNetworkInterfaces(token);

            return new MetadataResponse("aws", instanceId, region, machineType, interfaces);
        } catch (IOException | InterruptedException ex) {
            logger.warn("AWS metadata fetch failed: {}", ex.getMessage());
            throw new MetadataUnavailableException("Metadata service unavailable - not running on supported cloud platform", ex);
        }
    }

    private String fetchToken() throws IOException, InterruptedException {
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

    private String getMetadataValue(String path, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AWS_BASE + path))
                .timeout(Duration.ofSeconds(2))
                .header("X-aws-ec2-metadata-token", token)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.warn("IMDS request failed: path={} status={} body=\"{}\"", path, response.statusCode(), response.body());
            throw new IOException("IMDS request failed: " + response.statusCode());
        }
        return response.body().trim();
    }

    private String tryMetadataValue(String path, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AWS_BASE + path))
                .timeout(Duration.ofSeconds(2))
                .header("X-aws-ec2-metadata-token", token)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        String body = response.body();
        return body == null ? null : body.trim();
    }

    private List<NetworkInterfaceDto> fetchNetworkInterfaces(String token) throws IOException, InterruptedException {
        String macsRaw = getMetadataValue("/latest/meta-data/network/interfaces/macs/", token);
        String[] macs = macsRaw.split("\\n");

        List<NetworkInterfaceDto> interfaces = new ArrayList<>();
        for (String mac : macs) {
            String trimmed = mac.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String normalizedMac = trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;

            String privateIp = firstLine(tryMetadataValue("/latest/meta-data/network/interfaces/macs/" + normalizedMac + "/local-ipv4s", token));
            String publicIp = firstLine(tryMetadataValue("/latest/meta-data/network/interfaces/macs/" + normalizedMac + "/public-ipv4s", token));
            String vpcId = tryMetadataValue("/latest/meta-data/network/interfaces/macs/" + normalizedMac + "/vpc-id", token);

            interfaces.add(new NetworkInterfaceDto(privateIp, publicIp, vpcId));
        }

        return interfaces;
    }

    private String firstLine(String value) {
        if (value == null) {
            return null;
        }
        String[] lines = value.split("\\n");
        return lines.length > 0 ? lines[0].trim() : null;
    }

    private String toRegion(String availabilityZone) {
        if (availabilityZone == null || availabilityZone.isEmpty()) {
            return availabilityZone;
        }
        return availabilityZone.substring(0, availabilityZone.length() - 1);
    }
}
