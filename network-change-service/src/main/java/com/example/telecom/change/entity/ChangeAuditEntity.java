package com.example.telecom.change.entity;

import com.example.telecom.change.domain.AuditCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA entity representing an audit record for a change operation.
 * Each entity stores what action occurred, when, and who performed it.
 */
@Entity
@Table(name = "change_audit")
public class ChangeAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", nullable = false)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private AuditCategory category;

    @Column(name = "operator_id")
    private String operatorId;

    @Column(name = "details")
    private String details;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "success")
    private boolean success;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")
    private NetworkChangeEntity change;

    public ChangeAuditEntity() {}

    public ChangeAuditEntity(String action, AuditCategory category, String operatorId,
                             String details, boolean success) {
        this.action = action;
        this.category = category;
        this.operatorId = operatorId;
        this.details = details;
        this.success = success;
        this.recordedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public AuditCategory getCategory() { return category; }
    public void setCategory(AuditCategory category) { this.category = category; }
    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public NetworkChangeEntity getChange() { return change; }
    public void setChange(NetworkChangeEntity change) { this.change = change; }
}
