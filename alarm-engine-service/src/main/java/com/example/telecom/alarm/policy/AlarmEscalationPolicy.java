package com.example.telecom.alarm.policy;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.alarm.AlarmRecord;

public interface AlarmEscalationPolicy {
    boolean shouldEscalate(AlarmRecord alarm);
    EscalationLevel getLevel();
    /** Case B NOISE: evaluate method unrelated to RuleEvaluator */
    String evaluate(AlarmRecord alarm);
}
