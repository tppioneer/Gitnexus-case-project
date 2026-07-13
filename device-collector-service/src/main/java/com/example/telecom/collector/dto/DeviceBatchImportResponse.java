package com.example.telecom.collector.dto;

import java.util.ArrayList;
import java.util.List;

public class DeviceBatchImportResponse {

    private String batchId;
    private int totalRecords;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    private long processingTime;

    public DeviceBatchImportResponse() {
        this.errors = new ArrayList<>();
    }

    public DeviceBatchImportResponse(String batchId, int totalRecords, int successCount,
                                     int failureCount, List<String> errors, long processingTime) {
        this.batchId = batchId;
        this.totalRecords = totalRecords;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errors = errors != null ? errors : new ArrayList<>();
        this.processingTime = processingTime;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }

    public void recordSuccess() {
        this.successCount++;
        this.totalRecords++;
    }

    public void recordFailure(String error) {
        this.failureCount++;
        this.totalRecords++;
        this.errors.add(error);
    }
}
