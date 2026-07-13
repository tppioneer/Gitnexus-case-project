package com.example.telecom.device.lifecycle.dto;

import com.example.telecom.common.device.DeviceLifecycleStatus;

import java.time.LocalDateTime;

public class DeviceLifecycleResponse {
    private String deviceId;
    private DeviceLifecycleStatus status;
    private DeviceLifecycleStatus previousStatus;
    private String regionCode;
    private LocalDateTime timestamp;
    private String message;

    public DeviceLifecycleResponse() {}

    public DeviceLifecycleResponse(String deviceId, DeviceLifecycleStatus status,
                                    DeviceLifecycleStatus previousStatus, String regionCode,
                                    LocalDateTime timestamp, String message) {
        this.deviceId = deviceId;
        this.status = status;
        this.previousStatus = previousStatus;
        this.regionCode = regionCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    public static DeviceLifecycleResponse success(String deviceId, DeviceLifecycleStatus status,
                                                   String regionCode) {
        return new DeviceLifecycleResponse(deviceId, status, null, regionCode,
                LocalDateTime.now(), "Operation completed successfully");
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public DeviceLifecycleStatus getStatus() { return status; }
    public void setStatus(DeviceLifecycleStatus status) { this.status = status; }
    public DeviceLifecycleStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(DeviceLifecycleStatus previousStatus) { this.previousStatus = previousStatus; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
