package com.example.telecom.dispatch.domain;

import java.time.LocalDateTime;

public class DispatchAssignment {

    private final String assignmentId;
    private final String orderId;
    private final String assigneeId;
    private final String status;
    private final double score;
    private final LocalDateTime assignedAt;
    private final LocalDateTime completedAt;

    public DispatchAssignment(String assignmentId, String orderId, String assigneeId,
                              String status, double score, LocalDateTime assignedAt,
                              LocalDateTime completedAt) {
        this.assignmentId = assignmentId;
        this.orderId = orderId;
        this.assigneeId = assigneeId;
        this.status = status;
        this.score = score;
        this.assignedAt = assignedAt;
        this.completedAt = completedAt;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
