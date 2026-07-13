package com.example.telecom.alarm.federated.aggregation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import java.time.LocalDateTime;
import java.util.*;

public class RegionBasedAggregationStrategy implements AggregationStrategy {

    @Override
    public AggregationResult aggregate(List<FederatedAlarmRecord> alarms) {
        Map<String, Object> groupedData = new LinkedHashMap<>();

        Map<String, Long> regionCounts = new LinkedHashMap<>();
        Map<String, Map<String, Long>> regionSeverityDistribution = new LinkedHashMap<>();

        for (FederatedAlarmRecord alarm : alarms) {
            String region = alarm.getRegionCode() != null ? alarm.getRegionCode() : "UNKNOWN";
            regionCounts.merge(region, 1L, Long::sum);

            regionSeverityDistribution
                    .computeIfAbsent(region, k -> new LinkedHashMap<>())
                    .merge(alarm.getSeverity().name(), 1L, Long::sum);
        }

        groupedData.put("regionCounts", regionCounts);
        groupedData.put("regionSeverityDistribution", regionSeverityDistribution);
        groupedData.put("totalRegions", (long) regionCounts.size());

        Optional<Map.Entry<String, Long>> topRegion = regionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        topRegion.ifPresent(entry -> groupedData.put("mostAffectedRegion", entry.getKey()));

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
        return "REGION_BASED";
    }
}
