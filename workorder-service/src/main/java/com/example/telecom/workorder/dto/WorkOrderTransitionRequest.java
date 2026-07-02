package com.example.telecom.workorder.dto;

public class WorkOrderTransitionRequest {
    private String targetStatus;
    private String operator;

    public String getTargetStatus() { return targetStatus; }
    public void setTargetStatus(String targetStatus) { this.targetStatus = targetStatus; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
}
