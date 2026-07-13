package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    /**
     * Computes hourly distribution of alarms across the day (0-23).
     */
    public Map<Integer, Long> getHourlyDistribution() {
        return alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> toLocalDateTime(a.getCreatedTime()).getHour(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Computes daily distribution of alarms by day-of-week (1=Monday..7=Sunday).
     */
    public Map<Integer, Long> getDailyDistribution() {
        return alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> toLocalDateTime(a.getCreatedTime()).getDayOfWeek().getValue(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Computes severity trend as a map of severity to count.
     */
    public Map<Severity, Long> getSeverityTrend() {
        return alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        AlarmRecord::getSeverity,
                        () -> new EnumMap<>(Severity.class),
                        Collectors.counting()
                ));
    }

    /**
     * Returns the top N alarm types by occurrence count.
     */
    public List<Map.Entry<String, Long>> getTopAlarmTypes(int limit) {
        return alarmRepository.findAll().stream()
                .filter(a -> a.getMetricType() != null)
                .collect(Collectors.groupingBy(AlarmRecord::getMetricType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }
}
