package com.example.telecom.collector.dto;

public class MetricIngestRequest {
    private String vendor;
    private String rawMetricName;
    private double rawValue;
    private String rawUnit;
    private long timestamp;

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
