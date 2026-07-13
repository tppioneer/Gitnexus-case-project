package com.example.telecom.common.event;

import java.time.LocalTime;
import java.util.Objects;

public class NotificationRuleConfig {

    private final String configId;
    private final String userId;
    private final String channel;
    private final boolean enabled;
    private final String priorityThreshold;
    private final LocalTime quietHoursStart;
    private final LocalTime quietHoursEnd;

    public NotificationRuleConfig(String configId, String userId, String channel,
                                  boolean enabled, String priorityThreshold,
                                  LocalTime quietHoursStart, LocalTime quietHoursEnd) {
        this.configId = configId;
        this.userId = userId;
        this.channel = channel;
        this.enabled = enabled;
        this.priorityThreshold = priorityThreshold;
        this.quietHoursStart = quietHoursStart;
        this.quietHoursEnd = quietHoursEnd;
    }

    public String getConfigId() { return configId; }
    public String getUserId() { return userId; }
    public String getChannel() { return channel; }
    public boolean isEnabled() { return enabled; }
    public String getPriorityThreshold() { return priorityThreshold; }
    public LocalTime getQuietHoursStart() { return quietHoursStart; }
    public LocalTime getQuietHoursEnd() { return quietHoursEnd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRuleConfig that = (NotificationRuleConfig) o;
        return Objects.equals(configId, that.configId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configId);
    }

    @Override
    public String toString() {
        return "NotificationRuleConfig{" +
                "configId='" + configId + '\'' +
                ", userId='" + userId + '\'' +
                ", channel='" + channel + '\'' +
                ", enabled=" + enabled +
                ", priorityThreshold='" + priorityThreshold + '\'' +
                ", quietHoursStart=" + quietHoursStart +
                ", quietHoursEnd=" + quietHoursEnd +
                '}';
    }
}
