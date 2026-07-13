package com.example.telecom.gateway.dto;

public class DeviceHealthSummary {
    private long totalDevices;
    private long activeDevices;
    private long unhealthyDevices;
    /** Downstream field from DeviceInfo.maintenanceRegionCode chain */
    private String regionCode;

    public long getTotalDevices() { return totalDevices; }
    public void setTotalDevices(long totalDevices) { this.totalDevices = totalDevices; }
    public long getActiveDevices() { return activeDevices; }
    public void setActiveDevices(long activeDevices) { this.activeDevices = activeDevices; }
    public long getUnhealthyDevices() { return unhealthyDevices; }
    public void setUnhealthyDevices(long unhealthyDevices) { this.unhealthyDevices = unhealthyDevices; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
}
