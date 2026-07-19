package com.example.telecom.change.domain;

import java.time.Instant;

/**
 * Result of a rollback operation.
 */
public final class RollbackResult {
    private final boolean applied;
    private final String message;
    private final String handlerName;
    private final Instant completedAt;

    public RollbackResult(boolean applied, String message, String handlerName) {
        this(applied, message, handlerName, Instant.now());
    }

    public RollbackResult(boolean applied, String message, String handlerName, Instant completedAt) {
        this.applied = applied;
        this.message = message;
        this.handlerName = handlerName;
        this.completedAt = completedAt;
    }

    public boolean isApplied() { return applied; }
    public String getMessage() { return message; }
    public String getHandlerName() { return handlerName; }
    public Instant getCompletedAt() { return completedAt; }

    public static RollbackResult applied(String handler, String message) {
        return new RollbackResult(true, message, handler);
    }

    public static RollbackResult skipped() {
        return new RollbackResult(false, "rollback skipped", "none");
    }
}
