package com.example.telecom.common.maintenance;

public class MaintenanceWindowEvent {
    private String eventId;
    private String windowId;
    private String action; // CREATED, UPDATED, EXPIRED
    private String deviceId;
    private String regionCode;
    private long eventTimestamp;

    public MaintenanceWindowEvent() {}

    public MaintenanceWindowEvent(String eventId, String windowId, String action,
                                   String deviceId, String regionCode, long eventTimestamp) {
        this.eventId = eventId;
        this.windowId = windowId;
        this.action = action;
        this.deviceId = deviceId;
        this.regionCode = regionCode;
        this.eventTimestamp = eventTimestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getWindowId() { return windowId; }
    public void setWindowId(String windowId) { this.windowId = windowId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public long getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(long eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}
