package com.example.telecom.device.lifecycle.model;

import java.time.LocalDateTime;

public class DeviceFirmwareVersion {
    private String firmwareId;
    private String deviceId;
    private String version;
    private String previousVersion;
    private FirmwareUpgradeStatus status;
    private LocalDateTime upgradeTime;
    private String message;

    public DeviceFirmwareVersion() {}

    public DeviceFirmwareVersion(String firmwareId, String deviceId, String version,
                                  String previousVersion, FirmwareUpgradeStatus status,
                                  LocalDateTime upgradeTime, String message) {
        this.firmwareId = firmwareId;
        this.deviceId = deviceId;
        this.version = version;
        this.previousVersion = previousVersion;
        this.status = status;
        this.upgradeTime = upgradeTime;
        this.message = message;
    }

    public String getFirmwareId() { return firmwareId; }
    public void setFirmwareId(String firmwareId) { this.firmwareId = firmwareId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getPreviousVersion() { return previousVersion; }
    public void setPreviousVersion(String previousVersion) { this.previousVersion = previousVersion; }
    public FirmwareUpgradeStatus getStatus() { return status; }
    public void setStatus(FirmwareUpgradeStatus status) { this.status = status; }
    public LocalDateTime getUpgradeTime() { return upgradeTime; }
    public void setUpgradeTime(LocalDateTime upgradeTime) { this.upgradeTime = upgradeTime; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
