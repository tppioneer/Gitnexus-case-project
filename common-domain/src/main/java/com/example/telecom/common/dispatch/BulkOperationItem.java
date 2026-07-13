package com.example.telecom.common.dispatch;

import com.example.telecom.common.device.BulkOperationStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class BulkOperationItem {

    private final String itemId;
    private final String batchId;
    private final String targetId;
    private final String operation;
    private final BulkOperationStatus status;
    private final String errorMessage;
    private final LocalDateTime processedTime;

    public BulkOperationItem(String itemId, String batchId, String targetId,
                             String operation, BulkOperationStatus status,
                             String errorMessage, LocalDateTime processedTime) {
        this.itemId = itemId;
        this.batchId = batchId;
        this.targetId = targetId;
        this.operation = operation;
        this.status = status;
        this.errorMessage = errorMessage;
        this.processedTime = processedTime;
    }

    public String getItemId() { return itemId; }
    public String getBatchId() { return batchId; }
    public String getTargetId() { return targetId; }
    public String getOperation() { return operation; }
    public BulkOperationStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getProcessedTime() { return processedTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BulkOperationItem that = (BulkOperationItem) o;
        return Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "BulkOperationItem{" +
                "itemId='" + itemId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", operation='" + operation + '\'' +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", processedTime=" + processedTime +
                '}';
    }
}
