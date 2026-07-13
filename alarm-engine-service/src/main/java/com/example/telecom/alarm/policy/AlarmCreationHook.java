package com.example.telecom.alarm.policy;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.util.Objects;

/**
 * Lifecycle hook that processes alarms at creation time. Enriches the alarm
 * with additional metadata, logs the creation event, and triggers
 * notifications for high-severity alarms.
 */
public class AlarmCreationHook implements AlarmLifecycleHook {

    private final AlarmRepository alarmRepository;

    public AlarmCreationHook(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    @Override
    public void process(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");

        enrichAlarmMetadata(alarm);

        logCreation(alarm);

        if (alarm.getSeverity() == Severity.CRITICAL || alarm.getSeverity() == Severity.MAJOR) {
            triggerNotification(alarm);
        }
    }

    private void enrichAlarmMetadata(AlarmRecord alarm) {
        if (alarm.getDescription() == null || alarm.getDescription().isEmpty()) {
            alarm.setDescription("Alarm created for device " + alarm.getDeviceId()
                    + " type " + alarm.getMetricType());
        }
        alarm.setStatus(AlarmStatus.OPEN);
        if (alarm.getCreatedTime() <= 0) {
            alarm.setCreatedTime(System.currentTimeMillis());
        }
        alarm.setUpdatedTime(System.currentTimeMillis());
    }

    private void logCreation(AlarmRecord alarm) {
        String logEntry = "[CREATION] Alarm " + alarm.getAlarmId()
                + " created for device " + alarm.getDeviceId()
                + " severity=" + alarm.getSeverity()
                + " type=" + alarm.getMetricType()
                + " at " + alarm.getCreatedTime();
        System.out.println(logEntry);
    }

    private void triggerNotification(AlarmRecord alarm) {
        String notification = "[NOTIFICATION] High severity alarm " + alarm.getAlarmId()
                + " (" + alarm.getSeverity() + ") on device " + alarm.getDeviceId()
                + " - immediate attention required";
        System.out.println(notification);
    }
}
