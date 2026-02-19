package com.csye6225.webapp.service;

import com.csye6225.webapp.exception.MetadataUnavailableException;
import com.csye6225.webapp.model.MetadataResponse;
import com.csye6225.webapp.model.NetworkInterface;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class GcpMetadataService implements MetadataService {
    
    private static final String METADATA_BASE = "http://metadata.google.internal/computeMetadata/v1/instance/";
    private static final int TIMEOUT_MS = 2000;
    
    @Override
    public MetadataResponse getMetadata() {
        try {
            String instanceId = fetchMetadata("id");
            String zoneFull = fetchMetadata("zone");
            String machineTypeFull = fetchMetadata("machine-type");
            
            // Parse GCP fully qualified paths
            // zone: "projects/123456/zones/us-east1-b" -> "us-east1-b"
            String region = extractLastSegment(zoneFull);
            
            // machine-type: "projects/123456/machineTypes/e2-medium" -> "e2-medium"
            String machineType = extractLastSegment(machineTypeFull);
            
            // Get network interfaces
            List<NetworkInterface> interfaces = getNetworkInterfaces();
            
            return new MetadataResponse("gcp", instanceId, region, machineType, interfaces);
            
        } catch (Exception e) {
            throw new MetadataUnavailableException("Failed to retrieve GCP metadata", e);
        }
    }
    
    private List<NetworkInterface> getNetworkInterfaces() throws Exception {
        List<NetworkInterface> interfaces = new ArrayList<>();
        
        // Get number of network interfaces
        String interfacesData = fetchMetadata("network-interfaces/");
        String[] interfaceIndices = interfacesData.trim().split("\n");
        
        for (String index : interfaceIndices) {
            index = index.replace("/", "").trim();
            if (index.isEmpty()) continue;
            
            String privateIp = fetchMetadata("network-interfaces/" + index + "/ip");
            String networkFull = fetchMetadata("network-interfaces/" + index + "/network");
            String network = extractLastSegment(networkFull);
            
            String publicIp = null;
            try {
                publicIp = fetchMetadata("network-interfaces/" + index + "/access-configs/0/external-ip");
            } catch (Exception e) {
                // Public IP may not exist
            }
            
            interfaces.add(new NetworkInterface(privateIp, publicIp, network));
        }
        
        return interfaces;
    }
    
    /**
     * Extract last segment from GCP fully qualified path
     * Example: "projects/123/zones/us-east1-b" -> "us-east1-b"
     */
    private String extractLastSegment(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return fullPath;
        }
        String[] parts = fullPath.split("/");
        return parts[parts.length - 1];
    }
    
    private String fetchMetadata(String path) throws Exception {
        URL url = new URL(METADATA_BASE + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("Metadata-Flavor", "Google");  // Required for GCP
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Failed to fetch GCP metadata: " + path);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }
        
        reader.close();
        conn.disconnect();
        
        return response.toString().trim();
    }
}
