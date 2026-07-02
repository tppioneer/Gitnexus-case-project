package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Computes alarm statistics for dashboard and reporting.
 */
public class AlarmStatisticsService {

    private final AlarmRepository alarmRepository;

    public AlarmStatisticsService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public Map<Severity, Long> countBySeverity() {
        return alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(AlarmRecord::getSeverity, Collectors.counting()));
    }

    public Map<String, Long> countByDevice() {
        return alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(AlarmRecord::getDeviceId, Collectors.counting()));
    }

    public Map<String, Long> countByRegion() {
        return alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(AlarmRecord::getAlarmRegionCode, Collectors.counting()));
    }

    public long countActiveAlarms() {
        return alarmRepository.findAll().stream()
                .filter(a -> a.getStatus() == AlarmStatus.OPEN || a.getStatus() == AlarmStatus.ACKED)
                .count();
    }

    public long countBySeverityAndStatus(Severity severity, AlarmStatus status) {
        return alarmRepository.findAll().stream()
                .filter(a -> a.getSeverity() == severity && a.getStatus() == status)
                .count();
    }

    public List<AlarmRecord> findTopCriticalAlarms(int limit) {
        return alarmRepository.findAll().stream()
                .filter(a -> a.getSeverity() == Severity.CRITICAL)
                .filter(a -> a.getStatus() != AlarmStatus.CLEARED)
                .sorted(Comparator.comparingLong(AlarmRecord::getCreatedTime).reversed())
                .limit(limit)
                .toList();
    }

    public double averageAlarmsPerDevice() {
        Map<String, Long> byDevice = countByDevice();
        if (byDevice.isEmpty()) return 0.0;
        return byDevice.values().stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    public String generateReport() {
        long total = alarmRepository.findAll().size();
        long active = countActiveAlarms();
        Map<Severity, Long> bySeverity = countBySeverity();
        return String.format(
                "Alarm Report: total=%d, active=%d, critical=%d, major=%d, warning=%d, info=%d",
                total, active,
                bySeverity.getOrDefault(Severity.CRITICAL, 0L),
                bySeverity.getOrDefault(Severity.MAJOR, 0L),
                bySeverity.getOrDefault(Severity.WARNING, 0L),
                bySeverity.getOrDefault(Severity.INFO, 0L));
    }
}
