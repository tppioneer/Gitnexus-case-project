package com.example.telecom.common.device;

public class RawDeviceMetric {
    private String deviceId;
    private String vendor;
    private String rawMetricName;
    private double rawValue;
    private String rawUnit;
    private long timestamp;

    public RawDeviceMetric() {}

    public RawDeviceMetric(String deviceId, String vendor, String rawMetricName,
                           double rawValue, String rawUnit, long timestamp) {
        this.deviceId = deviceId;
        this.vendor = vendor;
        this.rawMetricName = rawMetricName;
        this.rawValue = rawValue;
        this.rawUnit = rawUnit;
        this.timestamp = timestamp;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getRawMetricName() { return rawMetricName; }
    public void setRawMetricName(String rawMetricName) { this.rawMetricName = rawMetricName; }
    public double getRawValue() { return rawValue; }
    public void setRawValue(double rawValue) { this.rawValue = rawValue; }
    public String getRawUnit() { return rawUnit; }
    public void setRawUnit(String rawUnit) { this.rawUnit = rawUnit; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
