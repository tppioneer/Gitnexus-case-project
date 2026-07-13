package com.example.telecom.alarm.federated.aggregation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import java.time.LocalDateTime;
import java.util.*;

public class SeverityBasedAggregationStrategy implements AggregationStrategy {

    @Override
    public AggregationResult aggregate(List<FederatedAlarmRecord> alarms) {
        Map<String, Object> groupedData = new LinkedHashMap<>();

        Map<String, Long> severityCounts = new LinkedHashMap<>();
        Map<String, Map<String, Long>> severityByRegion = new LinkedHashMap<>();

        for (FederatedAlarmRecord alarm : alarms) {
            String sev = alarm.getSeverity().name();
            severityCounts.merge(sev, 1L, Long::sum);

            severityByRegion
                    .computeIfAbsent(sev, k -> new LinkedHashMap<>())
                    .merge(alarm.getRegionCode() != null ? alarm.getRegionCode() : "UNKNOWN", 1L, Long::sum);
        }

        groupedData.put("severityCounts", severityCounts);
        groupedData.put("severityByRegion", severityByRegion);

        String predominantSeverity = severityCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NONE");
        groupedData.put("predominantSeverity", predominantSeverity);

        Map<String, Double> severityPercentage = new LinkedHashMap<>();
        long total = alarms.size();
        severityCounts.forEach((sev, count) ->
                severityPercentage.put(sev, total > 0 ? (count * 100.0 / total) : 0.0));
        groupedData.put("severityPercentage", severityPercentage);

        LocalDateTime earliest = alarms.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
        LocalDateTime latest = alarms.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .max(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());

        String timeRange = earliest + " / " + latest;

        return new AggregationResult(getType(), groupedData, alarms.size(), timeRange);
    }

    @Override
    public String getType() {
        return "SEVERITY_BASED";
    }
}
