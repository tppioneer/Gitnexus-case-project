package com.example.telecom.notification.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks channel health status, simulates health checks, and records failures/successes.
 */
public class NotificationChannelHealthCheck {

    private final Map<String, HealthStatus> healthStatuses = new ConcurrentHashMap<>();

    /**
     * Inner class representing the health status of a notification channel.
     */
    public static class HealthStatus {
        private boolean healthy;
        private long lastCheckTime;
        private int failureCount;
        private long latencyMs;
        private String errorMessage;

        public HealthStatus() {
            this.healthy = true;
            this.lastCheckTime = System.currentTimeMillis();
            this.failureCount = 0;
            this.latencyMs = 0;
            this.errorMessage = "";
        }

        public boolean isHealthy() {
            return healthy;
        }

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }

        public long getLastCheckTime() {
            return lastCheckTime;
        }

        public void setLastCheckTime(long lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }

        public long getLatencyMs() {
            return latencyMs;
        }

        public void setLatencyMs(long latencyMs) {
            this.latencyMs = latencyMs;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Simulates a health check for the given channel, always returning healthy with low latency.
     *
     * @param channel the channel name
     * @return the health status after the check
     */
    public HealthStatus checkHealth(String channel) {
        HealthStatus status = healthStatuses.computeIfAbsent(channel, k -> new HealthStatus());
        status.setHealthy(true);
        status.setLastCheckTime(System.currentTimeMillis());
        status.setLatencyMs(15L);
        status.setErrorMessage("");
        return status;
    }

    /**
     * Iterates all known channels and checks health for each.
     *
     * @return map of channel names to their health statuses
     */
    public Map<String, HealthStatus> checkAll() {
        Map<String, HealthStatus> results = new HashMap<>();
        for (String channel : healthStatuses.keySet()) {
            results.put(channel, checkHealth(channel));
        }
        return results;
    }

    /**
     * Returns the latest health status for the given channel.
     *
     * @param channel the channel name
     * @return the health status, or a default healthy status if none recorded
     */
    public HealthStatus getHealthStatus(String channel) {
        return healthStatuses.getOrDefault(channel, new HealthStatus());
    }

    /**
     * Returns all health statuses.
     *
     * @return map of all channel names to their health statuses
     */
    public Map<String, HealthStatus> getAllHealthStatus() {
        return new HashMap<>(healthStatuses);
    }

    /**
     * Updates health status with failure information.
     *
     * @param channel the channel name
     * @param error   the error message describing the failure
     */
    public void reportFailure(String channel, String error) {
        HealthStatus status = healthStatuses.computeIfAbsent(channel, k -> new HealthStatus());
        status.setHealthy(false);
        status.setLastCheckTime(System.currentTimeMillis());
        status.setFailureCount(status.getFailureCount() + 1);
        status.setErrorMessage(error);
    }

    /**
     * Updates health status with success information.
     *
     * @param channel the channel name
     * @param latency the latency in milliseconds
     */
    public void reportSuccess(String channel, long latency) {
        HealthStatus status = healthStatuses.computeIfAbsent(channel, k -> new HealthStatus());
        status.setHealthy(true);
        status.setLastCheckTime(System.currentTimeMillis());
        status.setLatencyMs(latency);
        status.setErrorMessage("");
    }
}
