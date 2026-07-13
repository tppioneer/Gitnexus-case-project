package com.example.telecom.device.lifecycle.model;

import java.time.LocalDateTime;

public class LifecycleRecord {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String vendor;
    private String model;
    private String regionCode;
    private String ipAddress;
    private DeviceLifecycleState currentState;
    private DeviceLifecycleState previousState;
    private LocalDateTime lastTransitionTime;
    private LocalDateTime createdTime;

    public LifecycleRecord() {}

    public LifecycleRecord(String deviceId, String deviceName, String deviceType, String vendor,
                            String model, String regionCode, String ipAddress,
                            DeviceLifecycleState currentState) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.vendor = vendor;
        this.model = model;
        this.regionCode = regionCode;
        this.ipAddress = ipAddress;
        this.currentState = currentState;
        this.previousState = null;
        this.lastTransitionTime = LocalDateTime.now();
        this.createdTime = LocalDateTime.now();
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public DeviceLifecycleState getCurrentState() { return currentState; }
    public void setCurrentState(DeviceLifecycleState currentState) { this.currentState = currentState; }
    public DeviceLifecycleState getPreviousState() { return previousState; }
    public void setPreviousState(DeviceLifecycleState previousState) { this.previousState = previousState; }
    public LocalDateTime getLastTransitionTime() { return lastTransitionTime; }
    public void setLastTransitionTime(LocalDateTime lastTransitionTime) { this.lastTransitionTime = lastTransitionTime; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
