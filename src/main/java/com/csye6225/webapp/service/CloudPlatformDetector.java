package com.csye6225.webapp.service;

import org.springframework.stereotype.Component;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class CloudPlatformDetector {
    
    private static final String AWS_METADATA_URL = "http://169.254.169.254/latest/meta-data/";
    private static final String AWS_TOKEN_URL = "http://169.254.169.254/latest/api/token";
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
        
        // Try AWS (with IMDSv2 support)
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
            // Try to fetch IMDSv2 token first
            String token = fetchIMDSv2Token();
            
            if (token != null) {
                // Use token for metadata request
                return testAwsMetadataWithToken(token);
            } else {
                // Fallback to IMDSv1 if token fetch fails
                return testAwsMetadataWithoutToken();
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    private String fetchIMDSv2Token() {
        try {
            URL url = new URL(AWS_TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", "60");
            conn.setDoOutput(true);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream())
                );
                String token = reader.readLine();
                reader.close();
                conn.disconnect();
                return token != null ? token.trim() : null;
            }
            conn.disconnect();
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean testAwsMetadataWithToken(String token) {
        try {
            URL url = new URL(AWS_METADATA_URL + "instance-id");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("X-aws-ec2-metadata-token", token);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean testAwsMetadataWithoutToken() {
        try {
            URL url = new URL(AWS_METADATA_URL + "instance-id");
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
