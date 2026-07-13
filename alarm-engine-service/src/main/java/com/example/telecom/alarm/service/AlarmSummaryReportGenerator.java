package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates summary reports for alarms based on various criteria such as
 * time period, severity, region, and trends.
 */
public class AlarmSummaryReportGenerator {

    private final AlarmRepository alarmRepository;

    public AlarmSummaryReportGenerator(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    /**
     * Generates a summary report for the given time period (e.g., "24h", "7d", "30d").
     */
    public String generate(String period) {
        Objects.requireNonNull(period, "period must not be null");
        long cutoff = parsePeriod(period);
        List<AlarmRecord> alarms = alarmRepository.findAll().stream()
                .filter(a -> a.getCreatedTime() >= cutoff)
                .collect(Collectors.toList());

        long total = alarms.size();
        long active = alarms.stream()
                .filter(a -> a.getStatus() == AlarmStatus.OPEN || a.getStatus() == AlarmStatus.ACKED)
                .count();
        Map<Severity, Long> bySeverity = alarms.stream()
                .collect(Collectors.groupingBy(AlarmRecord::getSeverity, Collectors.counting()));
        Set<String> devices = alarms.stream()
                .map(AlarmRecord::getDeviceId)
                .collect(Collectors.toSet());

        return String.format(
                "=== Alarm Summary Report [%s] ===\n" +
                "Total Alarms: %d\n" +
                "Active Alarms: %d\n" +
                "Affected Devices: %d\n" +
                "Critical: %d | Major: %d | Warning: %d | Info: %d\n" +
                "Generated: %d",
                period, total, active, devices.size(),
                bySeverity.getOrDefault(Severity.CRITICAL, 0L),
                bySeverity.getOrDefault(Severity.MAJOR, 0L),
                bySeverity.getOrDefault(Severity.WARNING, 0L),
                bySeverity.getOrDefault(Severity.INFO, 0L),
                System.currentTimeMillis()
        );
    }

    /**
     * Generates a report grouped by severity for all alarms.
     */
    public String generateBySeverity() {
        Map<Severity, Long> bySeverity = alarmRepository.findAll().stream()
                .collect(Collectors.groupingBy(AlarmRecord::getSeverity, Collectors.counting()));

        StringBuilder sb = new StringBuilder("=== Alarm Report by Severity ===\n");
        for (Severity severity : Severity.values()) {
            long count = bySeverity.getOrDefault(severity, 0L);
            sb.append(severity).append(": ").append(count).append("\n");
        }
        sb.append("Generated: ").append(System.currentTimeMillis());
        return sb.toString();
    }

    /**
     * Generates a report grouped by region for all alarms.
     */
    public String generateByRegion() {
        Map<String, Long> byRegion = alarmRepository.findAll().stream()
                .filter(a -> a.getAlarmRegionCode() != null)
                .collect(Collectors.groupingBy(AlarmRecord::getAlarmRegionCode, Collectors.counting()));

        StringBuilder sb = new StringBuilder("=== Alarm Report by Region ===\n");
        byRegion.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
        sb.append("Generated: ").append(System.currentTimeMillis());
        return sb.toString();
    }

    /**
     * Generates a trend report comparing alarm counts across periods.
     */
    public String generateTrendReport() {
        long now = System.currentTimeMillis();
        long dayMs = 24 * 3600 * 1000;

        long today = alarmRepository.findAll().stream()
                .filter(a -> a.getCreatedTime() >= now - dayMs)
                .count();
        long yesterday = alarmRepository.findAll().stream()
                .filter(a -> a.getCreatedTime() >= now - 2 * dayMs
                        && a.getCreatedTime() < now - dayMs)
                .count();
        long lastWeek = alarmRepository.findAll().stream()
                .filter(a -> a.getCreatedTime() >= now - 7 * dayMs)
                .count();

        return String.format(
                "=== Alarm Trend Report ===\n" +
                "Last 24h: %d\n" +
                "Previous 24h: %d\n" +
                "Last 7 days: %d\n" +
                "Change (24h vs previous): %+.1f%%\n" +
                "Generated: %d",
                today, yesterday, lastWeek,
                yesterday > 0 ? ((double)(today - yesterday) / yesterday) * 100 : 0.0,
                System.currentTimeMillis()
        );
    }

    private long parsePeriod(String period) {
        long now = System.currentTimeMillis();
        switch (period.toLowerCase()) {
            case "1h": return now - 3600 * 1000;
            case "24h": return now - 24 * 3600 * 1000;
            case "7d": return now - 7 * 24 * 3600 * 1000;
            case "30d": return now - 30 * 24 * 3600 * 1000;
            default: return now - 24 * 3600 * 1000;
        }
    }
}
