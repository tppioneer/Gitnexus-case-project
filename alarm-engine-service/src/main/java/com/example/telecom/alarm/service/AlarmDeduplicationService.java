package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;

/**
 * Deduplicates alarms to avoid creating duplicates for the same condition.
 */
public class AlarmDeduplicationService {

    private final AlarmRepository alarmRepository;

    public AlarmDeduplicationService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public boolean isDuplicate(AlarmRecord newAlarm) {
        List<AlarmRecord> deviceAlarms = alarmRepository.findByDeviceId(newAlarm.getDeviceId());
        return deviceAlarms.stream()
                .filter(a -> a.getStatus() != com.example.telecom.common.alarm.AlarmStatus.CLEARED)
                .anyMatch(a -> a.getMetricType().equals(newAlarm.getMetricType())
                        && a.getSeverity() == newAlarm.getSeverity()
                        && a.getDeviceId().equals(newAlarm.getDeviceId()));
    }
}
