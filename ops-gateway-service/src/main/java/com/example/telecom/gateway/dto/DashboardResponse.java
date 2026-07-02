package com.example.telecom.gateway.dto;

public class DashboardResponse {
    private DeviceHealthSummary deviceHealth;
    private AlarmSummary alarms;
    private WorkOrderSummary workOrders;
    private long timestamp;

    public DeviceHealthSummary getDeviceHealth() { return deviceHealth; }
    public void setDeviceHealth(DeviceHealthSummary deviceHealth) { this.deviceHealth = deviceHealth; }
    public AlarmSummary getAlarms() { return alarms; }
    public void setAlarms(AlarmSummary alarms) { this.alarms = alarms; }
    public WorkOrderSummary getWorkOrders() { return workOrders; }
    public void setWorkOrders(WorkOrderSummary workOrders) { this.workOrders = workOrders; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
