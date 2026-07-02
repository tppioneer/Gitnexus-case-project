package com.example.telecom.common.device;

public class DeviceMetricEvent {
    private String eventId;
    private String deviceId;
    private String metricId;
    private String metricType;
    private double value;
    private String unit;
    private long eventTimestamp;

    /** Downstream field: propagated from DeviceInfo.regionCode via mapper. Semantically different name. */
    private String deviceRegionCode;

    public DeviceMetricEvent() {}

    public DeviceMetricEvent(String eventId, String deviceId, String metricId, String metricType,
                             double value, String unit, long eventTimestamp, String deviceRegionCode) {
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.metricId = metricId;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.eventTimestamp = eventTimestamp;
        this.deviceRegionCode = deviceRegionCode;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public long getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(long eventTimestamp) { this.eventTimestamp = eventTimestamp; }
    public String getDeviceRegionCode() { return deviceRegionCode; }
    public void setDeviceRegionCode(String deviceRegionCode) { this.deviceRegionCode = deviceRegionCode; }
}
