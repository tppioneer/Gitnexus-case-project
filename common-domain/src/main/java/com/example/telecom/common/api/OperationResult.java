package com.example.telecom.common.api;

import java.util.*;

public class OperationResult {
    private boolean success;
    private String operation;
    private int totalItems;
    private int successCount;
    private int failureCount;
    private List<String> errors;

    public OperationResult() {
        this.errors = new ArrayList<>();
    }

    public OperationResult(String operation, int totalItems) {
        this.operation = operation;
        this.totalItems = totalItems;
        this.successCount = 0;
        this.failureCount = 0;
        this.errors = new ArrayList<>();
    }

    public void recordSuccess() { this.successCount++; this.success = true; }
    public void recordFailure(String error) {
        this.failureCount++;
        this.errors.add(error);
        this.success = false;
    }
    public void complete() { this.success = (failureCount == 0); }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
