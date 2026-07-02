package com.example.telecom.gateway.dto;

import java.util.Map;

/**
 * Regional dashboard summary aggregating all metrics for a region.
 */
public class DashboardRegionSummary {
    private String regionCode;
    private String regionName;
    private DeviceHealthSummary deviceHealth;
    private AlarmSummary alarms;
    private WorkOrderSummary workOrders;
    private long totalIncidents;
    private long resolvedIncidents;
    private double resolutionRate;
    private Map<String, Long> alarmTrendByHour;
    private long lastUpdated;

    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public DeviceHealthSummary getDeviceHealth() { return deviceHealth; }
    public void setDeviceHealth(DeviceHealthSummary deviceHealth) { this.deviceHealth = deviceHealth; }
    public AlarmSummary getAlarms() { return alarms; }
    public void setAlarms(AlarmSummary alarms) { this.alarms = alarms; }
    public WorkOrderSummary getWorkOrders() { return workOrders; }
    public void setWorkOrders(WorkOrderSummary workOrders) { this.workOrders = workOrders; }
    public long getTotalIncidents() { return totalIncidents; }
    public void setTotalIncidents(long totalIncidents) { this.totalIncidents = totalIncidents; }
    public long getResolvedIncidents() { return resolvedIncidents; }
    public void setResolvedIncidents(long resolvedIncidents) { this.resolvedIncidents = resolvedIncidents; }
    public double getResolutionRate() { return resolutionRate; }
    public void setResolutionRate(double resolutionRate) { this.resolutionRate = resolutionRate; }
    public Map<String, Long> getAlarmTrendByHour() { return alarmTrendByHour; }
    public void setAlarmTrendByHour(Map<String, Long> alarmTrendByHour) { this.alarmTrendByHour = alarmTrendByHour; }
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}
