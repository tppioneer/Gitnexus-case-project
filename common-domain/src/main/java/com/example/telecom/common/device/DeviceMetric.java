package com.example.telecom.common.device;

import java.time.Instant;

public class DeviceMetric {
    private String metricId;
    private String deviceId;
    private MetricType metricType;
    private double value;
    private String unit;
    private Instant collectedAt;
    private String vendor;
    private boolean normalized;

    public DeviceMetric() {}

    public DeviceMetric(String metricId, String deviceId, MetricType metricType, double value,
                        String unit, Instant collectedAt, String vendor) {
        this.metricId = metricId;
        this.deviceId = deviceId;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.collectedAt = collectedAt;
        this.vendor = vendor;
        this.normalized = false;
    }

    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType metricType) { this.metricType = metricType; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Instant getCollectedAt() { return collectedAt; }
    public void setCollectedAt(Instant collectedAt) { this.collectedAt = collectedAt; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public boolean isNormalized() { return normalized; }
    public void setNormalized(boolean normalized) { this.normalized = normalized; }
}
