package com.example.telecom.common.device;

import com.example.telecom.common.device.DeviceCertStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class DeviceCertificate {

    private final String certificateId;
    private final String deviceId;
    private final String serialNumber;
    private final String organization;
    private final String commonName;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final DeviceCertStatus status;
    private final String fingerprint;

    public DeviceCertificate(String certificateId, String deviceId, String serialNumber,
                             String organization, String commonName,
                             LocalDateTime issuedAt, LocalDateTime expiresAt,
                             DeviceCertStatus status, String fingerprint) {
        this.certificateId = certificateId;
        this.deviceId = deviceId;
        this.serialNumber = serialNumber;
        this.organization = organization;
        this.commonName = commonName;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.fingerprint = fingerprint;
    }

    public String getCertificateId() { return certificateId; }
    public String getDeviceId() { return deviceId; }
    public String getSerialNumber() { return serialNumber; }
    public String getOrganization() { return organization; }
    public String getCommonName() { return commonName; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public DeviceCertStatus getStatus() { return status; }
    public String getFingerprint() { return fingerprint; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceCertificate that = (DeviceCertificate) o;
        return Objects.equals(certificateId, that.certificateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateId);
    }

    @Override
    public String toString() {
        return "DeviceCertificate{" +
                "certificateId='" + certificateId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", organization='" + organization + '\'' +
                ", commonName='" + commonName + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", status=" + status +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
