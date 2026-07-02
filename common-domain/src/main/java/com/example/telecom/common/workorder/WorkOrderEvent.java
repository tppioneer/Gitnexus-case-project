package com.example.telecom.common.workorder;

public class WorkOrderEvent {
    private String eventId;
    private String workOrderId;
    private WorkOrderStatus fromStatus;
    private WorkOrderStatus toStatus;
    private String assignee;
    private String maintenanceRegionCode;
    private long eventTimestamp;

    public WorkOrderEvent() {}

    public WorkOrderEvent(String eventId, String workOrderId, WorkOrderStatus fromStatus,
                          WorkOrderStatus toStatus, String assignee, String maintenanceRegionCode,
                          long eventTimestamp) {
        this.eventId = eventId;
        this.workOrderId = workOrderId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.assignee = assignee;
        this.maintenanceRegionCode = maintenanceRegionCode;
        this.eventTimestamp = eventTimestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public WorkOrderStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(WorkOrderStatus fromStatus) { this.fromStatus = fromStatus; }
    public WorkOrderStatus getToStatus() { return toStatus; }
    public void setToStatus(WorkOrderStatus toStatus) { this.toStatus = toStatus; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public String getMaintenanceRegionCode() { return maintenanceRegionCode; }
    public void setMaintenanceRegionCode(String maintenanceRegionCode) { this.maintenanceRegionCode = maintenanceRegionCode; }
    public long getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(long eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}
