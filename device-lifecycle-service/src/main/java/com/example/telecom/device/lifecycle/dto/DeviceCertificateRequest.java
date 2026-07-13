package com.example.telecom.device.lifecycle.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class DeviceCertificateRequest {

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotBlank(message = "Certificate type is required")
    private String certificateType;

    @Min(value = 1, message = "Validity days must be positive")
    private int validityDays;

    private String organization;

    private String commonName;

    public DeviceCertificateRequest() {}

    public DeviceCertificateRequest(String deviceId, String certificateType, int validityDays,
                                     String organization, String commonName) {
        this.deviceId = deviceId;
        this.certificateType = certificateType;
        this.validityDays = validityDays;
        this.organization = organization;
        this.commonName = commonName;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }
}
