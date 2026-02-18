package com.csye6225.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MetadataResponse {
    
    @JsonProperty("cloud_platform")
    private String cloudPlatform;
    
    @JsonProperty("instance_id")
    private String instanceId;
    
    @JsonProperty("region")
    private String region;
    
    @JsonProperty("machine_type")
    private String machineType;
    
    @JsonProperty("network_interfaces")
    private List<NetworkInterface> networkInterfaces;
    
    public MetadataResponse() {}
    
    public MetadataResponse(String cloudPlatform, String instanceId, String region,
                           String machineType, List<NetworkInterface> networkInterfaces) {
        this.cloudPlatform = cloudPlatform;
        this.instanceId = instanceId;
        this.region = region;
        this.machineType = machineType;
        this.networkInterfaces = networkInterfaces;
    }
    
    // Getters and Setters
    public String getCloudPlatform() { return cloudPlatform; }
    public void setCloudPlatform(String cloudPlatform) { this.cloudPlatform = cloudPlatform; }
    
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getMachineType() { return machineType; }
    public void setMachineType(String machineType) { this.machineType = machineType; }
    
    public List<NetworkInterface> getNetworkInterfaces() { return networkInterfaces; }
    public void setNetworkInterfaces(List<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }
}
