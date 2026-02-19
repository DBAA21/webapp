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
public class AwsMetadataService implements MetadataService {
    
    private static final String METADATA_BASE = "http://169.254.169.254/latest/meta-data/";
    private static final int TIMEOUT_MS = 2000;
    
    @Override
    public MetadataResponse getMetadata() {
        try {
            String instanceId = fetchMetadata("instance-id");
            String availabilityZone = fetchMetadata("placement/availability-zone");
            String instanceType = fetchMetadata("instance-type");
            
            // Extract region from AZ (e.g., us-east-1a -> us-east-1)
            String region = availabilityZone.substring(0, availabilityZone.length() - 1);
            
            // Get network interfaces
            List<NetworkInterface> interfaces = getNetworkInterfaces();
            
            return new MetadataResponse("aws", instanceId, region, instanceType, interfaces);
            
        } catch (Exception e) {
            throw new MetadataUnavailableException("Failed to retrieve AWS metadata", e);
        }
    }
    
    private List<NetworkInterface> getNetworkInterfaces() throws Exception {
        List<NetworkInterface> interfaces = new ArrayList<>();
        
        // Get MAC addresses
        String macsData = fetchMetadata("network/interfaces/macs/");
        String[] macs = macsData.trim().split("\n");
        
        for (String mac : macs) {
            mac = mac.trim();
            if (mac.isEmpty()) continue;
            
            String privateIp = fetchMetadata("network/interfaces/macs/" + mac + "local-ipv4s");
            String publicIp = null;
            String vpcId = null;
            
            try {
                publicIp = fetchMetadata("network/interfaces/macs/" + mac + "public-ipv4s");
            } catch (Exception e) {
                // Public IP may not exist
            }
            
            try {
                vpcId = fetchMetadata("network/interfaces/macs/" + mac + "vpc-id");
            } catch (Exception e) {
                // VPC ID may not exist
            }
            
            interfaces.add(new NetworkInterface(privateIp, publicIp, vpcId));
        }
        
        return interfaces;
    }
    
    private String fetchMetadata(String path) throws Exception {
        URL url = new URL(METADATA_BASE + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Failed to fetch metadata: " + path);
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
