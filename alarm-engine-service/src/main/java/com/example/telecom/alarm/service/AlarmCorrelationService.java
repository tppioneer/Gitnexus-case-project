package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;

/**
 * Correlates a new alarm with existing alarms for the same device.
 */
public class AlarmCorrelationService {

    private final AlarmRepository alarmRepository;

    public AlarmCorrelationService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public List<AlarmRecord> correlate(AlarmRecord newAlarm) {
        return alarmRepository.findByDeviceId(newAlarm.getDeviceId()).stream()
                .filter(a -> !a.getAlarmId().equals(newAlarm.getAlarmId()))
                .filter(a -> a.getStatus() != com.example.telecom.common.alarm.AlarmStatus.CLEARED)
                .toList();
    }
}
