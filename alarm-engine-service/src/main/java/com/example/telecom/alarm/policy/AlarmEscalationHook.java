package com.example.telecom.alarm.policy;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.util.Objects;

/**
 * Lifecycle hook that processes alarms for escalation. Evaluates whether
 * an alarm should be escalated based on its severity, duration, and status.
 * Generates escalation notifications when appropriate.
 */
public class AlarmEscalationHook implements AlarmLifecycleHook {

    private final AlarmRepository alarmRepository;
    private static final long ESCALATION_THRESHOLD_CRITICAL_MS = 30 * 60 * 1000;
    private static final long ESCALATION_THRESHOLD_MAJOR_MS = 60 * 60 * 1000;

    public AlarmEscalationHook(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    @Override
    public void process(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");

        if (alarm.getStatus() == AlarmStatus.CLEARED) {
            return;
        }

        if (!shouldEscalate(alarm)) {
            return;
        }

        performEscalation(alarm);
    }

    private boolean shouldEscalate(AlarmRecord alarm) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - alarm.getCreatedTime();

        if (alarm.getSeverity() == Severity.CRITICAL) {
            return elapsed > ESCALATION_THRESHOLD_CRITICAL_MS;
        }
        if (alarm.getSeverity() == Severity.MAJOR) {
            return elapsed > ESCALATION_THRESHOLD_MAJOR_MS;
        }
        return false;
    }

    private void performEscalation(AlarmRecord alarm) {
        String escalationMessage = "[ESCALATION] Alarm " + alarm.getAlarmId()
                + " (" + alarm.getSeverity() + ") on device " + alarm.getDeviceId()
                + " has exceeded escalation threshold. Duration: "
                + (System.currentTimeMillis() - alarm.getCreatedTime()) / 1000 + "s";

        System.out.println(escalationMessage);

        alarm.setDescription(alarm.getDescription() != null
                ? alarm.getDescription() + " | ESCALATED: " + System.currentTimeMillis()
                : "ESCALATED: " + System.currentTimeMillis());
        alarm.setUpdatedTime(System.currentTimeMillis());
        alarmRepository.save(alarm);
    }
}
