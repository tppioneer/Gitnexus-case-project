package com.example.telecom.alarm.domain;

import java.util.Objects;

/**
 * Defines a triage rule that classifies alarms based on condition expressions,
 * assigning a priority, category, and optional auto-action.
 */
public class AlarmTriageRule {

    private String ruleId;
    private String name;
    private String conditionExpression;
    private int priority;
    private String category;
    private String autoAction;
    private boolean enabled;

    public AlarmTriageRule() {
    }

    public AlarmTriageRule(String ruleId, String name, String conditionExpression,
                            int priority, String category, String autoAction, boolean enabled) {
        this.ruleId = ruleId;
        this.name = name;
        this.conditionExpression = conditionExpression;
        this.priority = priority;
        this.category = category;
        this.autoAction = autoAction;
        this.enabled = enabled;
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

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAutoAction() {
        return autoAction;
    }

    public void setAutoAction(String autoAction) {
        this.autoAction = autoAction;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmTriageRule that = (AlarmTriageRule) o;
        return Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }

    @Override
    public String toString() {
        return "AlarmTriageRule{ruleId='" + ruleId + "', name='" + name + "', category='" + category + "'}";
    }
}
