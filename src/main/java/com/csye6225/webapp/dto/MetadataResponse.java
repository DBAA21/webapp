package com.csye6225.webapp.dto;

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
    private List<NetworkInterfaceDto> networkInterfaces;

    public MetadataResponse() {
    }

    public MetadataResponse(
            String cloudPlatform,
            String instanceId,
            String region,
            String machineType,
            List<NetworkInterfaceDto> networkInterfaces) {
        this.cloudPlatform = cloudPlatform;
        this.instanceId = instanceId;
        this.region = region;
        this.machineType = machineType;
        this.networkInterfaces = networkInterfaces;
    }

    public String getCloudPlatform() {
        return cloudPlatform;
    }

    public void setCloudPlatform(String cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public List<NetworkInterfaceDto> getNetworkInterfaces() {
        return networkInterfaces;
    }

    public void setNetworkInterfaces(List<NetworkInterfaceDto> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }
}
