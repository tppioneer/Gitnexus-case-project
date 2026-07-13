package com.example.telecom.alarm.policy;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Lifecycle hook that processes alarms when they are cleared. Updates
 * the alarm status, logs the clearing event, and checks for related
 * alarms that may also need to be cleared.
 */
public class AlarmClearedHook implements AlarmLifecycleHook {

    private final AlarmRepository alarmRepository;

    public AlarmClearedHook(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    @Override
    public void process(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");

        if (alarm.getStatus() != AlarmStatus.CLEARED) {
            alarm.setStatus(AlarmStatus.CLEARED);
            alarm.setUpdatedTime(System.currentTimeMillis());
            alarmRepository.save(alarm);
        }

        logClearance(alarm);

        checkRelatedAlarms(alarm);
    }

    private void logClearance(AlarmRecord alarm) {
        String logEntry = "[CLEARED] Alarm " + alarm.getAlarmId()
                + " for device " + alarm.getDeviceId()
                + " cleared at " + alarm.getUpdatedTime();
        System.out.println(logEntry);
    }

    private void checkRelatedAlarms(AlarmRecord alarm) {
        List<AlarmRecord> deviceAlarms = alarmRepository.findByDeviceId(alarm.getDeviceId());
        List<AlarmRecord> sameTypeActive = deviceAlarms.stream()
                .filter(a -> !a.getAlarmId().equals(alarm.getAlarmId()))
                .filter(a -> a.getMetricType() != null && a.getMetricType().equals(alarm.getMetricType()))
                .filter(a -> a.getStatus() == AlarmStatus.OPEN || a.getStatus() == AlarmStatus.ACKED)
                .collect(Collectors.toList());

        if (!sameTypeActive.isEmpty()) {
            String logEntry = "[RELATED] " + sameTypeActive.size()
                    + " active alarms of same type (" + alarm.getMetricType()
                    + ") still exist on device " + alarm.getDeviceId();
            System.out.println(logEntry);
        }
    }
}
