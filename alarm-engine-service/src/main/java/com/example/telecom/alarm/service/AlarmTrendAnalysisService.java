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
 * Service for analyzing alarm trends over time. Provides hourly and daily
 * distributions, severity trends, top alarm types, and peak prediction.
 */
public class AlarmTrendAnalysisService {

    private final AlarmRepository alarmRepository;

    public AlarmTrendAnalysisService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    /**
     * Analyzes alarm trends for the given time range (e.g., "24h", "7d", "30d").
     * Returns a map of analysis results keyed by analysis type.
     */
    public Map<String, Object> analyze(String timeRange) {
        Objects.requireNonNull(timeRange, "timeRange must not be null");
        long cutoff = parseTimeRange(timeRange);
        List<AlarmRecord> filtered = alarmRepository.findAll().stream()
                .filter(a -> a.getCreatedTime() >= cutoff)
                .collect(Collectors.toList());

        Map<String, Object> results = new LinkedHashMap<>();
        results.put("timeRange", timeRange);
        results.put("totalAlarms", filtered.size());
        results.put("severityTrend", getSeverityTrend(filtered));
        results.put("hourlyDistribution", getHourlyDistribution(filtered));
        results.put("dailyDistribution", getDailyDistribution(filtered));
        results.put("topAlarmTypes", getTopAlarmTypes(filtered, 10));
        results.put("activeAlarms", filtered.stream()
                .filter(a -> a.getStatus() == AlarmStatus.OPEN || a.getStatus() == AlarmStatus.ACKED)
                .count());
        return results;
    }

    /**
     * Computes the hourly distribution of alarms.
     * Returns a map of hour (0-23) to alarm count.
     */
    public Map<Integer, Long> getHourlyDistribution() {
        return getHourlyDistribution(alarmRepository.findAll());
    }

    private Map<Integer, Long> getHourlyDistribution(List<AlarmRecord> alarms) {
        return alarms.stream()
                .collect(Collectors.groupingBy(
                        a -> toLocalDateTime(a.getCreatedTime()).getHour(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Computes the daily distribution of alarms for the last 7 days.
     * Returns a map of day-of-week (1=Monday) to alarm count.
     */
    public Map<Integer, Long> getDailyDistribution() {
        return getDailyDistribution(alarmRepository.findAll());
    }

    private Map<Integer, Long> getDailyDistribution(List<AlarmRecord> alarms) {
        return alarms.stream()
                .collect(Collectors.groupingBy(
                        a -> toLocalDateTime(a.getCreatedTime()).getDayOfWeek().getValue(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Computes the severity trend as a map of severity to count.
     */
    public Map<Severity, Long> getSeverityTrend() {
        return getSeverityTrend(alarmRepository.findAll());
    }

    private Map<Severity, Long> getSeverityTrend(List<AlarmRecord> alarms) {
        return alarms.stream()
                .collect(Collectors.groupingBy(
                        AlarmRecord::getSeverity,
                        () -> new EnumMap<>(Severity.class),
                        Collectors.counting()
                ));
    }

    /**
     * Returns the top N alarm types by count.
     */
    public List<Map.Entry<String, Long>> getTopAlarmTypes(int limit) {
        return getTopAlarmTypes(alarmRepository.findAll(), limit);
    }

    private List<Map.Entry<String, Long>> getTopAlarmTypes(List<AlarmRecord> alarms, int limit) {
        return alarms.stream()
                .filter(a -> a.getMetricType() != null)
                .collect(Collectors.groupingBy(AlarmRecord::getMetricType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Predicts the next peak hour based on historical hourly distribution.
     * Returns the hour with the highest historical alarm count.
     */
    public int predictNextPeak() {
        Map<Integer, Long> hourly = getHourlyDistribution();
        return hourly.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    private long parseTimeRange(String timeRange) {
        long now = System.currentTimeMillis();
        switch (timeRange.toLowerCase()) {
            case "1h":
                return now - 3600 * 1000;
            case "24h":
                return now - 24 * 3600 * 1000;
            case "7d":
                return now - 7 * 24 * 3600 * 1000;
            case "30d":
                return now - 30 * 24 * 3600 * 1000;
            default:
                return now - 24 * 3600 * 1000;
        }
    }

    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }
}
