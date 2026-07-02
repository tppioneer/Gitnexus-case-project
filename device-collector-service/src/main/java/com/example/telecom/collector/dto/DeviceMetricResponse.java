package com.example.telecom.collector.dto;

public class DeviceMetricResponse {
    private String metricId;
    private String deviceId;
    private String metricType;
    private double value;
    private String unit;
    private long collectedAt;
    private boolean normalized;

    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public long getCollectedAt() { return collectedAt; }
    public void setCollectedAt(long collectedAt) { this.collectedAt = collectedAt; }
    public boolean isNormalized() { return normalized; }
    public void setNormalized(boolean normalized) { this.normalized = normalized; }
}
