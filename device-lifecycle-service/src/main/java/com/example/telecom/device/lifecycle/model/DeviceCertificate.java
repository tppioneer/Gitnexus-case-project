package com.example.telecom.device.lifecycle.model;

import java.time.LocalDate;

public class DeviceCertificate {
    private String certificateId;
    private String deviceId;
    private String serialNumber;
    private String certificateType;
    private String organization;
    private String commonName;
    private String fingerprint;
    private LocalDate issuedAt;
    private LocalDate expiresAt;
    private DeviceCertStatus status;

    public DeviceCertificate() {}

    public DeviceCertificate(String certificateId, String deviceId, String serialNumber,
                              String certificateType, String organization, String commonName,
                              String fingerprint, LocalDate issuedAt, LocalDate expiresAt,
                              DeviceCertStatus status) {
        this.certificateId = certificateId;
        this.deviceId = deviceId;
        this.serialNumber = serialNumber;
        this.certificateType = certificateType;
        this.organization = organization;
        this.commonName = commonName;
        this.fingerprint = fingerprint;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
    public LocalDate getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDate issuedAt) { this.issuedAt = issuedAt; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
    public DeviceCertStatus getStatus() { return status; }
    public void setStatus(DeviceCertStatus status) { this.status = status; }
}
