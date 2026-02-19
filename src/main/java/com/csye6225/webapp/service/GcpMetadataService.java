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
import org.springframework.stereotype.Service;

@Service
public class GcpMetadataService implements MetadataService {

    private static final String GCP_BASE = "http://metadata.google.internal/computeMetadata/v1/";

    private final HttpClient httpClient;

    public GcpMetadataService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Override
    public MetadataResponse getMetadata() {
        try {
            String instanceId = getMetadataValue("instance/id");
            String zonePath = getMetadataValue("instance/zone");
            String machinePath = getMetadataValue("instance/machine-type");

            String zone = lastSegment(zonePath);
            String machineType = lastSegment(machinePath);

            List<NetworkInterfaceDto> interfaces = fetchNetworkInterfaces();

            return new MetadataResponse("gcp", instanceId, zone, machineType, interfaces);
        } catch (IOException | InterruptedException ex) {
            throw new MetadataUnavailableException("Metadata service unavailable - not running on supported cloud platform", ex);
        }
    }

    private String getMetadataValue(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GCP_BASE + path))
                .timeout(Duration.ofSeconds(2))
                .header("Metadata-Flavor", "Google")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("GCP metadata request failed: " + response.statusCode());
        }
        return response.body().trim();
    }

    private String tryMetadataValue(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GCP_BASE + path))
                .timeout(Duration.ofSeconds(2))
                .header("Metadata-Flavor", "Google")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        String body = response.body();
        return body == null ? null : body.trim();
    }

    private List<NetworkInterfaceDto> fetchNetworkInterfaces() throws IOException, InterruptedException {
        String interfacesRaw = getMetadataValue("instance/network-interfaces/");
        String[] indices = interfacesRaw.split("\\n");

        List<NetworkInterfaceDto> interfaces = new ArrayList<>();
        for (String index : indices) {
            String trimmed = index.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String privateIp = tryMetadataValue("instance/network-interfaces/" + trimmed + "/ip");
            String publicIp = tryMetadataValue("instance/network-interfaces/" + trimmed + "/access-configs/0/external-ip");
            String networkPath = tryMetadataValue("instance/network-interfaces/" + trimmed + "/network");
            String network = lastSegment(networkPath);

            interfaces.add(new NetworkInterfaceDto(privateIp, publicIp, network));
        }

        return interfaces;
    }

    private String lastSegment(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] parts = value.split("/");
        return parts[parts.length - 1];
    }
}
