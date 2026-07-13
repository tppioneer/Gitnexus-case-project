package com.example.telecom.notification.dto;

/**
 * Response object for batch notification operations.
 * Contains summary statistics about a batch send including
 * total, success, and failure counts along with completion status.
 */
public class NotificationBatchResponse {

    private String batchId;
    private String channel;
    private int totalCount;
    private int successCount;
    private int failureCount;
    private String status;
    private long completedAt;

    public NotificationBatchResponse() {
    }

    public NotificationBatchResponse(String batchId, String channel, int totalCount,
                                     int successCount, int failureCount,
                                     String status, long completedAt) {
        this.batchId = batchId;
        this.channel = channel;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.status = status;
        this.completedAt = completedAt;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }
}
