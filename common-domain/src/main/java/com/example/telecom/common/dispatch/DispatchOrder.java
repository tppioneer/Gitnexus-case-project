package com.example.telecom.common.dispatch;

import com.example.telecom.common.dispatch.DispatchPriority;
import java.time.LocalDateTime;
import java.util.Objects;

public class DispatchOrder {

    private final String orderId;
    private final String workOrderId;
    private final DispatchPriority priority;
    private final String targetRegionCode;
    private final String assigneeId;
    private final String skillRequired;
    private final int estimatedDuration;
    private final String status;
    private final LocalDateTime createdTime;
    private final LocalDateTime assignedTime;
    private final LocalDateTime completedTime;

    public DispatchOrder(String orderId, String workOrderId, DispatchPriority priority,
                         String targetRegionCode, String assigneeId,
                         String skillRequired, int estimatedDuration,
                         String status, LocalDateTime createdTime,
                         LocalDateTime assignedTime, LocalDateTime completedTime) {
        this.orderId = orderId;
        this.workOrderId = workOrderId;
        this.priority = priority;
        this.targetRegionCode = targetRegionCode;
        this.assigneeId = assigneeId;
        this.skillRequired = skillRequired;
        this.estimatedDuration = estimatedDuration;
        this.status = status;
        this.createdTime = createdTime;
        this.assignedTime = assignedTime;
        this.completedTime = completedTime;
    }

    public String getOrderId() { return orderId; }
    public String getWorkOrderId() { return workOrderId; }
    public DispatchPriority getPriority() { return priority; }
    public String getTargetRegionCode() { return targetRegionCode; }
    public String getAssigneeId() { return assigneeId; }
    public String getSkillRequired() { return skillRequired; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getAssignedTime() { return assignedTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchOrder that = (DispatchOrder) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "DispatchOrder{" +
                "orderId='" + orderId + '\'' +
                ", workOrderId='" + workOrderId + '\'' +
                ", priority=" + priority +
                ", targetRegionCode='" + targetRegionCode + '\'' +
                ", assigneeId='" + assigneeId + '\'' +
                ", skillRequired='" + skillRequired + '\'' +
                ", estimatedDuration=" + estimatedDuration +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                ", assignedTime=" + assignedTime +
                ", completedTime=" + completedTime +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String orderId;
        private String workOrderId;
        private DispatchPriority priority;
        private String targetRegionCode;
        private String assigneeId;
        private String skillRequired;
        private int estimatedDuration;
        private String status;
        private LocalDateTime createdTime;
        private LocalDateTime assignedTime;
        private LocalDateTime completedTime;

        private Builder() {}

        public Builder orderId(String orderId) { this.orderId = orderId; return this; }
        public Builder workOrderId(String workOrderId) { this.workOrderId = workOrderId; return this; }
        public Builder priority(DispatchPriority priority) { this.priority = priority; return this; }
        public Builder targetRegionCode(String targetRegionCode) { this.targetRegionCode = targetRegionCode; return this; }
        public Builder assigneeId(String assigneeId) { this.assigneeId = assigneeId; return this; }
        public Builder skillRequired(String skillRequired) { this.skillRequired = skillRequired; return this; }
        public Builder estimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdTime(LocalDateTime createdTime) { this.createdTime = createdTime; return this; }
        public Builder assignedTime(LocalDateTime assignedTime) { this.assignedTime = assignedTime; return this; }
        public Builder completedTime(LocalDateTime completedTime) { this.completedTime = completedTime; return this; }

        public DispatchOrder build() {
            return new DispatchOrder(orderId, workOrderId, priority, targetRegionCode,
                    assigneeId, skillRequired, estimatedDuration, status,
                    createdTime, assignedTime, completedTime);
        }
    }
}
