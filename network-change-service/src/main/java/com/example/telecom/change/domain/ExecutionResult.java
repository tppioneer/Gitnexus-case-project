package com.example.telecom.change.domain;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Result returned from a change execution attempt. Captures success/failure,
 * timing, and arbitrary output data produced by the executor.
 */
public final class ExecutionResult {
    private final boolean success;
    private final String message;
    private final String executorName;
    private final Instant completedAt;
    private final Map<String, Object> outputs;

    public ExecutionResult(boolean success, String message, String executorName) {
        this(success, message, executorName, Instant.now(), Collections.emptyMap());
    }

    public ExecutionResult(boolean success, String message, String executorName,
                           Instant completedAt, Map<String, Object> outputs) {
        this.success = success;
        this.message = message;
        this.executorName = Objects.requireNonNull(executorName, "executorName");
        this.completedAt = completedAt;
        this.outputs = outputs == null ? Collections.emptyMap() : new HashMap<>(outputs);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getExecutorName() { return executorName; }
    public Instant getCompletedAt() { return completedAt; }
    public Map<String, Object> getOutputs() { return Collections.unmodifiableMap(outputs); }

    public static ExecutionResult success(String executorName, String message) {
        return new ExecutionResult(true, message, executorName);
    }

    public static ExecutionResult failure(String executorName, String message) {
        return new ExecutionResult(false, message, executorName);
    }

    @Override
    public String toString() {
        return "ExecutionResult{success=" + success + ", executor='" + executorName + "', msg='" + message + "'}";
    }
}
