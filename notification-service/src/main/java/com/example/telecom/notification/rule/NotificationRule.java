package com.example.telecom.notification.rule;

public class NotificationRule {
    private String ruleId;
    private String eventType; // WORK_ORDER_STATUS_CHANGE, ALARM_CREATED, SLA_BREACH
    private String channel;
    private String recipientTemplate;
    private boolean enabled;

    public NotificationRule() {}

    public NotificationRule(String ruleId, String eventType, String channel,
                             String recipientTemplate, boolean enabled) {
        this.ruleId = ruleId;
        this.eventType = eventType;
        this.channel = channel;
        this.recipientTemplate = recipientTemplate;
        this.enabled = enabled;
    }

    public boolean matches(String eventType) {
        return enabled && this.eventType.equals(eventType);
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getRecipientTemplate() { return recipientTemplate; }
    public void setRecipientTemplate(String recipientTemplate) { this.recipientTemplate = recipientTemplate; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
