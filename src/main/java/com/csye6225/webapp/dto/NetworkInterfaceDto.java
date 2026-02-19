package com.csye6225.webapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkInterfaceDto {

    @JsonProperty("private_ip")
    private String privateIp;

    @JsonProperty("public_ip")
    private String publicIp;

    @JsonProperty("network")
    private String network;

    public NetworkInterfaceDto() {
    }

    public NetworkInterfaceDto(String privateIp, String publicIp, String network) {
        this.privateIp = privateIp;
        this.publicIp = publicIp;
        this.network = network;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
