package com.example.telecom.notification.service;

import com.example.telecom.common.workorder.WorkOrderEvent;

public class NotificationTemplateService {

    public String renderSubject(WorkOrderEvent event) {
        return "[Telecom Ops] Work Order " + event.getWorkOrderId()
                + " status: " + event.getFromStatus() + " → " + event.getToStatus();
    }

    public String renderBody(WorkOrderEvent event) {
        return "Work Order: " + event.getWorkOrderId() + "\n"
                + "Status transition: " + event.getFromStatus() + " → " + event.getToStatus() + "\n"
                + "Assignee: " + (event.getAssignee() != null ? event.getAssignee() : "unassigned") + "\n"
                + "Region: " + event.getMaintenanceRegionCode() + "\n"
                + "Time: " + event.getEventTimestamp();
    }
}
