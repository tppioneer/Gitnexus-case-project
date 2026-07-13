package com.example.telecom.gateway.client;

import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;

/**
 * Simulated client for alarm-engine-service.
 */
public class AlarmClient {

    public List<AlarmRecord> fetchActiveAlarmSummary() {
        return List.of();
    }

    public AlarmRecord fetchAlarm(String alarmId) {
        return null;
    }

    public List<AlarmRecord> getAllAlarms() {
        return fetchActiveAlarmSummary();
    }
}
