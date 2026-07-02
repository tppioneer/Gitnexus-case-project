package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;

/**
 * Manages alarm lifecycle: ack and clear operations.
 */
public class AlarmLifecycleService {

    private final AlarmRepository alarmRepository;

    public AlarmLifecycleService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public AlarmRecord acknowledge(String alarmId) {
        AlarmRecord alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("Alarm not found: " + alarmId));
        alarm.setStatus(AlarmStatus.ACKED);
        alarm.setUpdatedTime(System.currentTimeMillis());
        return alarmRepository.save(alarm);
    }

    public AlarmRecord clear(String alarmId) {
        AlarmRecord alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("Alarm not found: " + alarmId));
        alarm.setStatus(AlarmStatus.CLEARED);
        alarm.setUpdatedTime(System.currentTimeMillis());
        return alarmRepository.save(alarm);
    }
}
