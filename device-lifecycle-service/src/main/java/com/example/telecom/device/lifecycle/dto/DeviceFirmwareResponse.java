package com.example.telecom.device.lifecycle.dto;

import com.example.telecom.device.lifecycle.model.FirmwareUpgradeStatus;

import java.time.LocalDateTime;

public class DeviceFirmwareResponse {
    private String deviceId;
    private String currentVersion;
    private String targetVersion;
    private FirmwareUpgradeStatus status;
    private LocalDateTime upgradeTime;
    private String message;

    public DeviceFirmwareResponse() {}

    public DeviceFirmwareResponse(String deviceId, String currentVersion, String targetVersion,
                                   FirmwareUpgradeStatus status, LocalDateTime upgradeTime, String message) {
        this.deviceId = deviceId;
        this.currentVersion = currentVersion;
        this.targetVersion = targetVersion;
        this.status = status;
        this.upgradeTime = upgradeTime;
        this.message = message;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }
    public String getTargetVersion() { return targetVersion; }
    public void setTargetVersion(String targetVersion) { this.targetVersion = targetVersion; }
    public FirmwareUpgradeStatus getStatus() { return status; }
    public void setStatus(FirmwareUpgradeStatus status) { this.status = status; }
    public LocalDateTime getUpgradeTime() { return upgradeTime; }
    public void setUpgradeTime(LocalDateTime upgradeTime) { this.upgradeTime = upgradeTime; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
