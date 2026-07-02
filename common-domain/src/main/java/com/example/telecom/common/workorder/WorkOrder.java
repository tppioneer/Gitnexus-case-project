package com.example.telecom.common.workorder;

public class WorkOrder {
    private String workOrderId;
    private String alarmId;
    private String deviceId;
    private WorkOrderStatus status;
    private WorkOrderPriority priority;
    private String title;
    private String description;

    /** Downstream field: propagated from AlarmEvent.deviceRegionCode. Semantically different name. */
    private String maintenanceRegionCode;

    private String assignee;
    private long createdTime;
    private long updatedTime;

    public WorkOrder() {}

    public WorkOrder(String workOrderId, String alarmId, String deviceId, WorkOrderStatus status,
                     WorkOrderPriority priority, String title, String description,
                     String maintenanceRegionCode, long createdTime) {
        this.workOrderId = workOrderId;
        this.alarmId = alarmId;
        this.deviceId = deviceId;
        this.status = status;
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.maintenanceRegionCode = maintenanceRegionCode;
        this.createdTime = createdTime;
        this.updatedTime = createdTime;
    }

    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public WorkOrderStatus getStatus() { return status; }
    public void setStatus(WorkOrderStatus status) { this.status = status; }
    public WorkOrderPriority getPriority() { return priority; }
    public void setPriority(WorkOrderPriority priority) { this.priority = priority; }
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
