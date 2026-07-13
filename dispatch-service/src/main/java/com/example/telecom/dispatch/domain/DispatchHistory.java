package com.example.telecom.dispatch.domain;

import java.time.LocalDateTime;

public class DispatchHistory {

    private String historyId;
    private String orderId;
    private String workOrderId;
    private String assigneeId;
    private String previousAssigneeId;
    private String action;
    private LocalDateTime timestamp;
    private String details;

    public DispatchHistory(String historyId, String orderId, String workOrderId,
                           String assigneeId, String previousAssigneeId,
                           String action, LocalDateTime timestamp, String details) {
        this.historyId = historyId;
        this.orderId = orderId;
        this.workOrderId = workOrderId;
        this.assigneeId = assigneeId;
        this.previousAssigneeId = previousAssigneeId;
        this.action = action;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getPreviousAssigneeId() {
        return previousAssigneeId;
    }

    public void setPreviousAssigneeId(String previousAssigneeId) {
        this.previousAssigneeId = previousAssigneeId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
