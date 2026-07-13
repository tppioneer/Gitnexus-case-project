package com.example.telecom.common.device;

import java.time.LocalDateTime;
import java.util.Objects;

public class DeviceFirmwareVersion {

    private final String firmwareId;
    private final String deviceId;
    private final String version;
    private final String previousVersion;
    private final LocalDateTime releaseDate;
    private final String status;
    private final LocalDateTime installedAt;

    public DeviceFirmwareVersion(String firmwareId, String deviceId, String version,
                                 String previousVersion, LocalDateTime releaseDate,
                                 String status, LocalDateTime installedAt) {
        this.firmwareId = firmwareId;
        this.deviceId = deviceId;
        this.version = version;
        this.previousVersion = previousVersion;
        this.releaseDate = releaseDate;
        this.status = status;
        this.installedAt = installedAt;
    }

    public String getFirmwareId() { return firmwareId; }
    public String getDeviceId() { return deviceId; }
    public String getVersion() { return version; }
    public String getPreviousVersion() { return previousVersion; }
    public LocalDateTime getReleaseDate() { return releaseDate; }
    public String getStatus() { return status; }
    public LocalDateTime getInstalledAt() { return installedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceFirmwareVersion that = (DeviceFirmwareVersion) o;
        return Objects.equals(firmwareId, that.firmwareId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firmwareId);
    }

    @Override
    public String toString() {
        return "DeviceFirmwareVersion{" +
                "firmwareId='" + firmwareId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", version='" + version + '\'' +
                ", previousVersion='" + previousVersion + '\'' +
                ", releaseDate=" + releaseDate +
                ", status='" + status + '\'' +
                ", installedAt=" + installedAt +
                '}';
    }
}
