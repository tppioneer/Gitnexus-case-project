package com.example.telecom.device.lifecycle.dto;

import com.example.telecom.device.lifecycle.model.UpgradeMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class DeviceFirmwareRequest {

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotBlank(message = "Target version is required")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Version must follow semantic versioning (e.g., 2.1.0)")
    private String targetVersion;

    private UpgradeMode upgradeMode;

    private LocalDateTime scheduledTime;

    public DeviceFirmwareRequest() {}

    public DeviceFirmwareRequest(String deviceId, String targetVersion,
                                  UpgradeMode upgradeMode, LocalDateTime scheduledTime) {
        this.deviceId = deviceId;
        this.targetVersion = targetVersion;
        this.upgradeMode = upgradeMode;
        this.scheduledTime = scheduledTime;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getTargetVersion() { return targetVersion; }
    public void setTargetVersion(String targetVersion) { this.targetVersion = targetVersion; }
    public UpgradeMode getUpgradeMode() { return upgradeMode; }
    public void setUpgradeMode(UpgradeMode upgradeMode) { this.upgradeMode = upgradeMode; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
}
