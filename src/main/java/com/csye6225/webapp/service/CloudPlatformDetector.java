package com.csye6225.webapp.service;

import org.springframework.stereotype.Component;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class CloudPlatformDetector {
    
    private static final String AWS_METADATA_URL = "http://169.254.169.254/latest/meta-data/";
    private static final String GCP_METADATA_URL = "http://metadata.google.internal/computeMetadata/v1/";
    private static final int TIMEOUT_MS = 2000; // 2 seconds
    
    private String detectedPlatform = null;
    
    /**
     * Detect cloud platform at startup (cached for application lifetime)
     */
    public String detectPlatform() {
        if (detectedPlatform != null) {
            return detectedPlatform;
        }
        
        // Try GCP first
        if (isGcpMetadataAvailable()) {
            detectedPlatform = "gcp";
            return detectedPlatform;
        }
        
        // Try AWS
        if (isAwsMetadataAvailable()) {
            detectedPlatform = "aws";
            return detectedPlatform;
        }
        
        // No cloud platform detected
        return null;
    }
    
    private boolean isGcpMetadataAvailable() {
        try {
            URL url = new URL(GCP_METADATA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Metadata-Flavor", "Google");
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isAwsMetadataAvailable() {
        try {
            URL url = new URL(AWS_METADATA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
