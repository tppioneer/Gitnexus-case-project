package com.example.telecom.workorder.dto;

public class WorkOrderResponse {
    private String workOrderId;
    private String alarmId;
    private String deviceId;
    private String status;
    private String priority;
    private String title;
    private String description;
    private String maintenanceRegionCode;
    private String assignee;
    private long createdTime;
    private long updatedTime;

    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMaintenanceRegionCode() { return maintenanceRegionCode; }
    public void setMaintenanceRegionCode(String maintenanceRegionCode) { this.maintenanceRegionCode = maintenanceRegionCode; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    public long getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(long updatedTime) { this.updatedTime = updatedTime; }
}
