package com.example.telecom.common.audit;

public class AuditEntry {
    private String auditId;
    private String entityType;
    private String entityId;
    private String action;
    private String operator;
    private String details;
    private long timestamp;

    public AuditEntry() {}

    public AuditEntry(String auditId, String entityType, String entityId, String action,
                      String operator, String details, long timestamp) {
        this.auditId = auditId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.operator = operator;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
