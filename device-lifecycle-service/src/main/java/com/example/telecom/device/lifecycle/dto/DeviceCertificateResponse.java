package com.example.telecom.device.lifecycle.dto;

import com.example.telecom.device.lifecycle.model.DeviceCertStatus;

import java.time.LocalDate;

public class DeviceCertificateResponse {
    private String deviceId;
    private String certificateId;
    private String serialNumber;
    private LocalDate issuedAt;
    private LocalDate expiresAt;
    private DeviceCertStatus status;
    private String fingerprint;

    public DeviceCertificateResponse() {}

    public DeviceCertificateResponse(String deviceId, String certificateId, String serialNumber,
                                      LocalDate issuedAt, LocalDate expiresAt,
                                      DeviceCertStatus status, String fingerprint) {
        this.deviceId = deviceId;
        this.certificateId = certificateId;
        this.serialNumber = serialNumber;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.fingerprint = fingerprint;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public LocalDate getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDate issuedAt) { this.issuedAt = issuedAt; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
    public DeviceCertStatus getStatus() { return status; }
    public void setStatus(DeviceCertStatus status) { this.status = status; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
}
