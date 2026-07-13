package com.example.telecom.workorder.domain;

public class WorkOrderHistory {

    private String historyId;
    private String workOrderId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String operatorId;
    private String comment;
    private long createdTime;

    public WorkOrderHistory() {
    }

    public WorkOrderHistory(String historyId, String workOrderId, String fieldName,
                            String oldValue, String newValue, String operatorId,
                            String comment, long createdTime) {
        this.historyId = historyId;
        this.workOrderId = workOrderId;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.operatorId = operatorId;
        this.comment = comment;
        this.createdTime = createdTime;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
