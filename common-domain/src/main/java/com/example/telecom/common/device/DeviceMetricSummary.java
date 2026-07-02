package com.example.telecom.common.device;

import java.util.Map;

public class DeviceMetricSummary {
    private String deviceId;
    private String deviceRegionCode;
    private Map<String, Double> latestValues;
    private Map<String, Double> averageValues;
    private Map<String, Double> maxValues;
    private Map<String, Double> minValues;
    private int sampleCount;
    private long windowStartMs;
    private long windowEndMs;

    public DeviceMetricSummary() {}

    public DeviceMetricSummary(String deviceId, String deviceRegionCode, int sampleCount) {
        this.deviceId = deviceId;
        this.deviceRegionCode = deviceRegionCode;
        this.sampleCount = sampleCount;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceRegionCode() { return deviceRegionCode; }
    public void setDeviceRegionCode(String deviceRegionCode) { this.deviceRegionCode = deviceRegionCode; }
    public Map<String, Double> getLatestValues() { return latestValues; }
    public void setLatestValues(Map<String, Double> latestValues) { this.latestValues = latestValues; }
    public Map<String, Double> getAverageValues() { return averageValues; }
    public void setAverageValues(Map<String, Double> averageValues) { this.averageValues = averageValues; }
    public Map<String, Double> getMaxValues() { return maxValues; }
    public void setMaxValues(Map<String, Double> maxValues) { this.maxValues = maxValues; }
    public Map<String, Double> getMinValues() { return minValues; }
    public void setMinValues(Map<String, Double> minValues) { this.minValues = minValues; }
    public int getSampleCount() { return sampleCount; }
    public void setSampleCount(int sampleCount) { this.sampleCount = sampleCount; }
    public long getWindowStartMs() { return windowStartMs; }
    public void setWindowStartMs(long windowStartMs) { this.windowStartMs = windowStartMs; }
    public long getWindowEndMs() { return windowEndMs; }
    public void setWindowEndMs(long windowEndMs) { this.windowEndMs = windowEndMs; }
}
