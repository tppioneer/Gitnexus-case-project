package com.example.telecom.notification.policy;

import com.example.telecom.notification.domain.FailedNotification;

public class NotificationRetryPolicy {

    private final int maxRetries;
    private final long baseDelayMs;
    private final long maxDelayMs;
    private final String retryStrategy;

    public NotificationRetryPolicy(int maxRetries, long baseDelayMs,
                                    long maxDelayMs, String retryStrategy) {
        this.maxRetries = maxRetries;
        this.baseDelayMs = baseDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.retryStrategy = retryStrategy;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryDelay(int attempt) {
        if ("EXPONENTIAL".equalsIgnoreCase(retryStrategy)) {
            long delay = baseDelayMs * (long) Math.pow(2, attempt - 1);
            return Math.min(delay, maxDelayMs);
        }
        return baseDelayMs;
    }

    public boolean shouldRetry(FailedNotification notification) {
        return notification.getAttemptCount() < maxRetries && !isExpired(notification);
    }

    public String getRetryStrategy() {
        return retryStrategy;
    }

    public boolean isExpired(FailedNotification notification) {
        long now = System.currentTimeMillis();
        long twentyFourHoursMs = 24L * 60 * 60 * 1000;
        return (now - notification.getCreatedAt()) > twentyFourHoursMs;
    }
}
