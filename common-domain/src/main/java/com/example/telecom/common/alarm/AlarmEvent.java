package com.example.telecom.common.alarm;

public class AlarmEvent {
    private String eventId;
    private String alarmId;
    private String deviceId;
    private Severity severity;
    private AlarmStatus status;

    /** Downstream field: propagated from AlarmRecord.alarmRegionCode. */
    private String deviceRegionCode;

    private long eventTimestamp;

    public AlarmEvent() {}

    public AlarmEvent(String eventId, String alarmId, String deviceId, Severity severity,
                      AlarmStatus status, String deviceRegionCode, long eventTimestamp) {
        this.eventId = eventId;
        this.alarmId = alarmId;
        this.deviceId = deviceId;
        this.severity = severity;
        this.status = status;
        this.deviceRegionCode = deviceRegionCode;
        this.eventTimestamp = eventTimestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public AlarmStatus getStatus() { return status; }
    public void setStatus(AlarmStatus status) { this.status = status; }
    public String getDeviceRegionCode() { return deviceRegionCode; }
    public void setDeviceRegionCode(String deviceRegionCode) { this.deviceRegionCode = deviceRegionCode; }
    public long getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(long eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}
