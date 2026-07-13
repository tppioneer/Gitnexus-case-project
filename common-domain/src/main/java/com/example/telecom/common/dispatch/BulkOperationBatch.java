package com.example.telecom.common.dispatch;

import com.example.telecom.common.device.BulkOperationStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class BulkOperationBatch {

    private final String batchId;
    private final String operationType;
    private final int itemCount;
    private final BulkOperationStatus status;
    private final LocalDateTime createdTime;
    private final LocalDateTime completedTime;
    private final String createdBy;

    public BulkOperationBatch(String batchId, String operationType, int itemCount,
                              BulkOperationStatus status, LocalDateTime createdTime,
                              LocalDateTime completedTime, String createdBy) {
        this.batchId = batchId;
        this.operationType = operationType;
        this.itemCount = itemCount;
        this.status = status;
        this.createdTime = createdTime;
        this.completedTime = completedTime;
        this.createdBy = createdBy;
    }

    public String getBatchId() { return batchId; }
    public String getOperationType() { return operationType; }
    public int getItemCount() { return itemCount; }
    public BulkOperationStatus getStatus() { return status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public String getCreatedBy() { return createdBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BulkOperationBatch that = (BulkOperationBatch) o;
        return Objects.equals(batchId, that.batchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchId);
    }

    @Override
    public String toString() {
        return "BulkOperationBatch{" +
                "batchId='" + batchId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", itemCount=" + itemCount +
                ", status=" + status +
                ", createdTime=" + createdTime +
                ", completedTime=" + completedTime +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
