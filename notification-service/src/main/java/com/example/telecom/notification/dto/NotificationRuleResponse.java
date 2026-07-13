package com.example.telecom.notification.dto;

public class NotificationRuleResponse {

    private String ruleId;
    private String name;
    private String userId;
    private String channel;
    private String eventType;
    private String priorityThreshold;
    private boolean enabled;
    private long timeWindow;
    private String createdTime;

    public NotificationRuleResponse() {
    }

    public NotificationRuleResponse(String ruleId, String name, String userId,
                                    String channel, String eventType, String priorityThreshold,
                                    boolean enabled, long timeWindow, String createdTime) {
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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public static NotificationRuleResponse fromDomain(com.example.telecom.notification.domain.NotificationRule rule) {
        NotificationRuleResponse response = new NotificationRuleResponse();
        response.setRuleId(rule.getRuleId());
        response.setName(rule.getName());
        response.setUserId(rule.getUserId());
        response.setChannel(rule.getChannel());
        response.setEventType(rule.getEventType());
        response.setPriorityThreshold(rule.getPriorityThreshold());
        response.setEnabled(rule.isEnabled());
        response.setTimeWindow(rule.getTimeWindow());
        response.setCreatedTime(rule.getCreatedTime() != null ? rule.getCreatedTime().toString() : null);
        return response;
    }

    @Override
    public String toString() {
        return "NotificationRuleResponse{"
                + "ruleId='" + ruleId + '\''
                + ", name='" + name + '\''
                + ", userId='" + userId + '\''
                + ", channel='" + channel + '\''
                + ", eventType='" + eventType + '\''
                + ", priorityThreshold='" + priorityThreshold + '\''
                + ", enabled=" + enabled
                + ", timeWindow=" + timeWindow
                + ", createdTime='" + createdTime + '\''
                + '}';
    }
}
