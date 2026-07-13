package com.example.telecom.notification.dto;

public class NotificationRuleRequest {

    private String name;
    private String userId;
    private String channel;
    private String eventType;
    private String priorityThreshold;
    private long timeWindow;

    public NotificationRuleRequest() {
    }

    public NotificationRuleRequest(String name, String userId, String channel,
                                   String eventType, String priorityThreshold, long timeWindow) {
        this.name = name;
        this.userId = userId;
        this.channel = channel;
        this.eventType = eventType;
        this.priorityThreshold = priorityThreshold;
        this.timeWindow = timeWindow;
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

    public long getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty()
                && userId != null && !userId.trim().isEmpty()
                && channel != null && !channel.trim().isEmpty()
                && eventType != null && !eventType.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "NotificationRuleRequest{"
                + "name='" + name + '\''
                + ", userId='" + userId + '\''
                + ", channel='" + channel + '\''
                + ", eventType='" + eventType + '\''
                + ", priorityThreshold='" + priorityThreshold + '\''
                + ", timeWindow=" + timeWindow
                + '}';
    }
}
