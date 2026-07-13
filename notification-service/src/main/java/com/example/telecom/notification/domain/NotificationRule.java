package com.example.telecom.notification.domain;

import java.time.LocalDateTime;

public class NotificationRule {

    private String ruleId;
    private String name;
    private String userId;
    private String channel;
    private String eventType;
    private String priorityThreshold;
    private boolean enabled;
    private long timeWindow;
    private LocalDateTime createdTime;

    public NotificationRule() {
    }

    public NotificationRule(String ruleId, String name, String userId, String channel,
                            String eventType, String priorityThreshold, boolean enabled,
                            long timeWindow, LocalDateTime createdTime) {
        this.ruleId = ruleId;
        this.name = name;
        this.userId = userId;
        this.channel = channel;
        this.eventType = eventType;
        this.priorityThreshold = priorityThreshold;
        this.enabled = enabled;
        this.timeWindow = timeWindow;
        this.createdTime = createdTime;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPriorityThreshold() {
        return priorityThreshold;
    }

    public void setPriorityThreshold(String priorityThreshold) {
        this.priorityThreshold = priorityThreshold;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "NotificationRule{" +
                "ruleId='" + ruleId + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", channel='" + channel + '\'' +
                ", eventType='" + eventType + '\'' +
                ", priorityThreshold='" + priorityThreshold + '\'' +
                ", enabled=" + enabled +
                ", timeWindow=" + timeWindow +
                ", createdTime=" + createdTime +
                '}';
    }
}
