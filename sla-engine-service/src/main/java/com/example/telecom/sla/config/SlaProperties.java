package com.example.telecom.sla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sla")
public class SlaProperties {

    private long monitoringIntervalMinutes = 5;
    private long breachCheckIntervalMinutes = 1;
    private long escalationIntervalMinutes = 10;
    private int maxEscalationLevel = 3;

    public long getMonitoringIntervalMinutes() {
        return monitoringIntervalMinutes;
    }

    public void setMonitoringIntervalMinutes(long monitoringIntervalMinutes) {
        this.monitoringIntervalMinutes = monitoringIntervalMinutes;
    }

    public long getBreachCheckIntervalMinutes() {
        return breachCheckIntervalMinutes;
    }

    public void setBreachCheckIntervalMinutes(long breachCheckIntervalMinutes) {
        this.breachCheckIntervalMinutes = breachCheckIntervalMinutes;
    }

    public long getEscalationIntervalMinutes() {
        return escalationIntervalMinutes;
    }

    public void setEscalationIntervalMinutes(long escalationIntervalMinutes) {
        this.escalationIntervalMinutes = escalationIntervalMinutes;
    }

    public int getMaxEscalationLevel() {
        return maxEscalationLevel;
    }

    public void setMaxEscalationLevel(int maxEscalationLevel) {
        this.maxEscalationLevel = maxEscalationLevel;
    }
}
