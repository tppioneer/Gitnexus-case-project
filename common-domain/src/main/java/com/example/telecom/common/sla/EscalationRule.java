package com.example.telecom.common.sla;

import com.example.telecom.common.EscalationLevel;
import java.util.List;
import java.util.Objects;

public class EscalationRule {

    private final String ruleId;
    private final String name;
    private final String condition;
    private final EscalationLevel escalationLevel;
    private final int timeoutMinutes;
    private final List<String> notifyChannels;

    public EscalationRule(String ruleId, String name, String condition,
                          EscalationLevel escalationLevel, int timeoutMinutes,
                          List<String> notifyChannels) {
        this.ruleId = ruleId;
        this.name = name;
        this.condition = condition;
        this.escalationLevel = escalationLevel;
        this.timeoutMinutes = timeoutMinutes;
        this.notifyChannels = notifyChannels;
    }

    public String getRuleId() { return ruleId; }
    public String getName() { return name; }
    public String getCondition() { return condition; }
    public EscalationLevel getEscalationLevel() { return escalationLevel; }
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public List<String> getNotifyChannels() { return notifyChannels; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscalationRule that = (EscalationRule) o;
        return Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }

    @Override
    public String toString() {
        return "EscalationRule{" +
                "ruleId='" + ruleId + '\'' +
                ", name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", escalationLevel=" + escalationLevel +
                ", timeoutMinutes=" + timeoutMinutes +
                ", notifyChannels=" + notifyChannels +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String ruleId;
        private String name;
        private String condition;
        private EscalationLevel escalationLevel;
        private int timeoutMinutes;
        private List<String> notifyChannels;

        private Builder() {}

        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder condition(String condition) { this.condition = condition; return this; }
        public Builder escalationLevel(EscalationLevel escalationLevel) { this.escalationLevel = escalationLevel; return this; }
        public Builder timeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; return this; }
        public Builder notifyChannels(List<String> notifyChannels) { this.notifyChannels = notifyChannels; return this; }

        public EscalationRule build() {
            return new EscalationRule(ruleId, name, condition, escalationLevel,
                    timeoutMinutes, notifyChannels);
        }
    }
}
