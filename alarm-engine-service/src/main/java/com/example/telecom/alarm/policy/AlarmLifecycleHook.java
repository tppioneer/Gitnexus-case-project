package com.example.telecom.alarm.policy;

import com.example.telecom.common.alarm.AlarmRecord;

/**
 * Interface for lifecycle hooks that process alarms at various stages
 * of their lifecycle (creation, clearing, escalation).
 */
public interface AlarmLifecycleHook {

    /**
     * Process an alarm at the appropriate lifecycle stage.
     *
     * @param alarm the alarm to process
     */
    void process(AlarmRecord alarm);
}
