package com.example.telecom.alarm.repository;

import com.example.telecom.common.alarm.AlarmRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AlarmRepository {
    private final Map<String, AlarmRecord> alarms = new ConcurrentHashMap<>();

    public AlarmRecord save(AlarmRecord alarm) {
        alarms.put(alarm.getAlarmId(), alarm);
        return alarm;
    }

    public Optional<AlarmRecord> findById(String alarmId) {
        return Optional.ofNullable(alarms.get(alarmId));
    }

    public List<AlarmRecord> findByDeviceId(String deviceId) {
        return alarms.values().stream()
                .filter(a -> deviceId.equals(a.getDeviceId()))
                .toList();
    }

    public List<AlarmRecord> findAll() {
        return new ArrayList<>(alarms.values());
    }

    public List<AlarmRecord> findActiveAlarms() {
        return alarms.values().stream()
                .filter(a -> a.getStatus() == com.example.telecom.common.alarm.AlarmStatus.OPEN
                        || a.getStatus() == com.example.telecom.common.alarm.AlarmStatus.ACKED)
                .toList();
    }
}
