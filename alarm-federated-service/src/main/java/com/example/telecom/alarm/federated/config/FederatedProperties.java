package com.example.telecom.alarm.federated.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alarm.federated")
public class FederatedProperties {

    private int dedupWindowMinutes = 5;
    private int correlationWindowMinutes = 30;
    private int escalationIntervalMinutes = 15;
    private int maxSourcesPerRegion = 20;
    private int healthCheckIntervalSeconds = 60;

    public int getDedupWindowMinutes() {
        return dedupWindowMinutes;
    }

    public void setDedupWindowMinutes(int dedupWindowMinutes) {
        this.dedupWindowMinutes = dedupWindowMinutes;
    }

    public int getCorrelationWindowMinutes() {
        return correlationWindowMinutes;
    }

    public void setCorrelationWindowMinutes(int correlationWindowMinutes) {
        this.correlationWindowMinutes = correlationWindowMinutes;
    }

    public int getEscalationIntervalMinutes() {
        return escalationIntervalMinutes;
    }

    public void setEscalationIntervalMinutes(int escalationIntervalMinutes) {
        this.escalationIntervalMinutes = escalationIntervalMinutes;
    }

    public int getMaxSourcesPerRegion() {
        return maxSourcesPerRegion;
    }

    public void setMaxSourcesPerRegion(int maxSourcesPerRegion) {
        this.maxSourcesPerRegion = maxSourcesPerRegion;
    }

    public int getHealthCheckIntervalSeconds() {
        return healthCheckIntervalSeconds;
    }

    public void setHealthCheckIntervalSeconds(int healthCheckIntervalSeconds) {
        this.healthCheckIntervalSeconds = healthCheckIntervalSeconds;
    }
}
