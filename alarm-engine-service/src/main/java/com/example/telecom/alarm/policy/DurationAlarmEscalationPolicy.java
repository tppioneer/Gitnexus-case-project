package com.example.telecom.alarm.policy;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.alarm.AlarmRecord;

public class DurationAlarmEscalationPolicy implements AlarmEscalationPolicy {

    private final long durationThresholdMs;

    public DurationAlarmEscalationPolicy(long durationThresholdMs) {
        this.durationThresholdMs = durationThresholdMs;
    }

    @Override
    public boolean shouldEscalate(AlarmRecord alarm) {
        long duration = System.currentTimeMillis() - alarm.getCreatedTime();
        return duration > durationThresholdMs;
    }

    @Override
    public EscalationLevel getLevel() {
        return EscalationLevel.LEVEL_1;
    }

    @Override
    public String evaluate(AlarmRecord alarm) {
        long duration = System.currentTimeMillis() - alarm.getCreatedTime();
        return duration > durationThresholdMs
                ? "ESCALATE: Alarm " + alarm.getAlarmId() + " exceeded duration threshold ("
                    + (duration / 60000) + " min)"
                : "OK: Alarm " + alarm.getAlarmId() + " within duration threshold";
    }
}
