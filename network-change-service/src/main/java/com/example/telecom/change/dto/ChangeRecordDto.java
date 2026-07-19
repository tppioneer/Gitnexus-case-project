package com.example.telecom.change.dto;

import java.time.LocalDateTime;

/**
 * Data-transfer object that carries a {@code tableName} field.
 * Not annotated with {@code @Entity}; purely a reporting DTO.
 */
public class ChangeRecordDto {
    private Long id;
    private String tableName;
    private String changeId;
    private String action;
    private LocalDateTime recordedAt;

    public ChangeRecordDto() {}

    public ChangeRecordDto(Long id, String tableName, String changeId, String action, LocalDateTime recordedAt) {
        this.id = id;
        this.tableName = tableName;
        this.changeId = changeId;
        this.action = action;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getChangeId() { return changeId; }
    public void setChangeId(String changeId) { this.changeId = changeId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
