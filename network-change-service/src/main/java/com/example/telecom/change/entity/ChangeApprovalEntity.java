package com.example.telecom.change.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA entity representing an approval record for a change.
 */
@Entity
@Table(name = "change_approval")
public class ChangeApprovalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approver_id", nullable = false)
    private String approverId;

    @Column(name = "approved")
    private boolean approved;

    @Column(name = "comment")
    private String comment;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    public ChangeApprovalEntity() {}

    public ChangeApprovalEntity(String approverId, boolean approved, String comment) {
        this.approverId = approverId;
        this.approved = approved;
        this.comment = comment;
        this.decidedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
}
