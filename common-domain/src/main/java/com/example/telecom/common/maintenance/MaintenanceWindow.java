package com.example.telecom.common.maintenance;

public class MaintenanceWindow {
    private String windowId;
    private String deviceId;
    /** NOT Case C target — different semantic from DeviceInfo.regionCode */
    private String regionCode;
    private long startTime;
    private long endTime;
    private String reason;
    private boolean active;

    public MaintenanceWindow() {}

    public MaintenanceWindow(String windowId, String deviceId, String regionCode,
                              long startTime, long endTime, String reason, boolean active) {
        this.windowId = windowId;
        this.deviceId = deviceId;
        this.regionCode = regionCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.active = active;
    }

    public boolean coversTime(long timestamp) {
        return active && timestamp >= startTime && timestamp <= endTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }

    public String getWindowId() { return windowId; }
    public void setWindowId(String windowId) { this.windowId = windowId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
