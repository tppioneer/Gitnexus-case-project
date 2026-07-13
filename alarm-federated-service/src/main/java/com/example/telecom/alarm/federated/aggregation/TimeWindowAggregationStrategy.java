package com.example.telecom.alarm.federated.aggregation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TimeWindowAggregationStrategy implements AggregationStrategy {

    private final long windowMinutes;

    public TimeWindowAggregationStrategy() {
        this.windowMinutes = 60;
    }

    public TimeWindowAggregationStrategy(long windowMinutes) {
        this.windowMinutes = windowMinutes;
    }

    @Override
    public AggregationResult aggregate(List<FederatedAlarmRecord> alarms) {
        Map<String, Object> groupedData = new LinkedHashMap<>();

        if (alarms.isEmpty()) {
            groupedData.put("windows", Collections.emptyMap());
            groupedData.put("trend", "NO_DATA");
            return new AggregationResult(getType(), groupedData, 0, "N/A");
        }

        LocalDateTime earliest = alarms.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
        LocalDateTime latest = alarms.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .max(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());

        Map<String, Long> windowCounts = new LinkedHashMap<>();
        Map<String, Map<String, Long>> windowSeverity = new LinkedHashMap<>();

        for (FederatedAlarmRecord alarm : alarms) {
            String windowKey = getWindowKey(alarm.getAlarmTime(), earliest);
            windowCounts.merge(windowKey, 1L, Long::sum);
            windowSeverity
                    .computeIfAbsent(windowKey, k -> new LinkedHashMap<>())
                    .merge(alarm.getSeverity().name(), 1L, Long::sum);
        }

        groupedData.put("windows", windowCounts);
        groupedData.put("windowSeverity", windowSeverity);
        groupedData.put("windowSizeMinutes", windowMinutes);

        List<Long> counts = new ArrayList<>(windowCounts.values());
        String trend = determineTrend(counts);
        groupedData.put("trend", trend);

        String timeRange = earliest + " / " + latest;

        return new AggregationResult(getType(), groupedData, alarms.size(), timeRange);
    }

    private String getWindowKey(LocalDateTime alarmTime, LocalDateTime baseTime) {
        long minutesSinceBase = ChronoUnit.MINUTES.between(baseTime, alarmTime);
        long windowIndex = minutesSinceBase / windowMinutes;
        return baseTime.plusMinutes(windowIndex * windowMinutes).toString();
    }

    private String determineTrend(List<Long> counts) {
        if (counts.size() < 2) {
            return "STABLE";
        }
        long firstHalf = counts.subList(0, counts.size() / 2).stream().mapToLong(Long::longValue).sum();
        long secondHalf = counts.subList(counts.size() / 2, counts.size()).stream().mapToLong(Long::longValue).sum();
        if (secondHalf > firstHalf * 1.2) {
            return "INCREASING";
        } else if (secondHalf < firstHalf * 0.8) {
            return "DECREASING";
        }
        return "STABLE";
    }

    @Override
    public String getType() {
        return "TIME_WINDOW";
    }
}
