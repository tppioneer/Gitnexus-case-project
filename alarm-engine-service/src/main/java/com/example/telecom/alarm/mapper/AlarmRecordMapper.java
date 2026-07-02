package com.example.telecom.alarm.mapper;

import com.example.telecom.common.alarm.*;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.device.MetricType;

import java.time.Instant;
import java.util.UUID;

/**
 * Maps between device metric events, alarm records, and alarm events.
 * EXPLICITLY maps deviceRegionCode → alarmRegionCode for Case C field propagation.
 */
public class AlarmRecordMapper {

    /**
     * Creates a minimal DeviceMetric from a DeviceMetricEvent for rule evaluation.
     */
    public DeviceMetric toDeviceMetric(DeviceMetricEvent event) {
        return new DeviceMetric(
                event.getMetricId(),
                event.getDeviceId(),
                MetricType.valueOf(event.getMetricType()),
                event.getValue(),
                event.getUnit(),
                Instant.ofEpochMilli(event.getEventTimestamp()),
                "unknown"
        );
    }

    /**
     * Creates an AlarmRecord with EXPLICIT field mapping from DeviceMetricEvent.
     * DeviceMetricEvent.deviceRegionCode → AlarmRecord.alarmRegionCode
     */
    public AlarmRecord toAlarmRecord(DeviceMetricEvent event, EvaluationResult result, Severity severity) {
        return new AlarmRecord(
                UUID.randomUUID().toString(),
                event.getDeviceId(),
                event.getMetricId(),
                event.getMetricType(),
                severity,
                AlarmStatus.OPEN,
                result.getMessage(),
                event.getDeviceRegionCode(),  // ← EXPLICIT: deviceRegionCode → alarmRegionCode
                System.currentTimeMillis()
        );
    }

    /**
     * Creates an AlarmEvent with EXPLICIT field mapping from AlarmRecord.
     * AlarmRecord.alarmRegionCode → AlarmEvent.deviceRegionCode
     */
    public AlarmEvent toAlarmEvent(AlarmRecord alarm) {
        return new AlarmEvent(
                UUID.randomUUID().toString(),
                alarm.getAlarmId(),
                alarm.getDeviceId(),
                alarm.getSeverity(),
                alarm.getStatus(),
                alarm.getAlarmRegionCode(),  // ← EXPLICIT: alarmRegionCode → event.deviceRegionCode
                System.currentTimeMillis()
        );
    }
}
