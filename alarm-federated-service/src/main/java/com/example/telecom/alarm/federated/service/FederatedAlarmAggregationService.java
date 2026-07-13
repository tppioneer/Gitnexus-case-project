package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmSummary;
import com.example.telecom.alarm.federated.aggregation.*;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FederatedAlarmAggregationService {

    private final FederatedAlarmRepository alarmRepository;
    private final RegionBasedAggregationStrategy regionStrategy;
    private final SeverityBasedAggregationStrategy severityStrategy;
    private final TimeWindowAggregationStrategy timeWindowStrategy;

    private FederatedAlarmSummary cachedSummary;
    private LocalDateTime lastSummaryUpdate;

    public FederatedAlarmAggregationService(FederatedAlarmRepository alarmRepository,
                                             RegionBasedAggregationStrategy regionStrategy,
                                             SeverityBasedAggregationStrategy severityStrategy,
                                             TimeWindowAggregationStrategy timeWindowStrategy) {
        this.alarmRepository = alarmRepository;
        this.regionStrategy = regionStrategy;
        this.severityStrategy = severityStrategy;
        this.timeWindowStrategy = timeWindowStrategy;
    }

    public AggregationResult aggregate(List<FederatedAlarmRecord> alarms) {
        return severityStrategy.aggregate(alarms);
    }

    public AggregationResult aggregateByRegion(List<FederatedAlarmRecord> alarms) {
        return regionStrategy.aggregate(alarms);
    }

    public AggregationResult aggregateBySeverity(List<FederatedAlarmRecord> alarms) {
        return severityStrategy.aggregate(alarms);
    }

    public AggregationResult aggregateByTimeWindow(List<FederatedAlarmRecord> alarms, Duration window) {
        TimeWindowAggregationStrategy strategy = new TimeWindowAggregationStrategy(window.toMinutes());
        return strategy.aggregate(alarms);
    }

    public FederatedAlarmSummary getAggregationSummary() {
        if (cachedSummary != null && lastSummaryUpdate != null
                && Duration.between(lastSummaryUpdate, LocalDateTime.now()).toSeconds() < 30) {
            return cachedSummary;
        }

        List<FederatedAlarmRecord> allAlarms = alarmRepository.findAll();

        Map<String, Long> bySeverity = allAlarms.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSeverity() != null ? a.getSeverity().name() : "UNKNOWN",
                        Collectors.counting()
                ));

        Map<String, Long> byRegion = allAlarms.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getRegionCode() != null ? a.getRegionCode() : "UNKNOWN",
                        Collectors.counting()
                ));

        LocalDateTime earliest = allAlarms.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
        LocalDateTime latest = allAlarms.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .max(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());

        String timeRange = earliest + " / " + latest;

        cachedSummary = new FederatedAlarmSummary(
                allAlarms.size(),
                bySeverity,
                byRegion,
                timeRange,
                LocalDateTime.now()
        );
        lastSummaryUpdate = LocalDateTime.now();

        return cachedSummary;
    }

    public AggregationStrategy resolveStrategy(AggregationType type) {
        return switch (type) {
            case REGION -> regionStrategy;
            case SEVERITY -> severityStrategy;
            case TIME_WINDOW -> timeWindowStrategy;
        };
    }

    public FederatedAlarmRecord mergeAlarms(FederatedAlarmRecord primary, FederatedAlarmRecord secondary) {
        if (primary == null) {
            return secondary;
        }
        if (secondary == null) {
            return primary;
        }

        if (secondary.getSeverity() != null
                && (primary.getSeverity() == null
                || secondary.getSeverity().ordinal() < primary.getSeverity().ordinal())) {
            primary.setSeverity(secondary.getSeverity());
        }

        if (primary.getAlarmTime() == null
                || (secondary.getAlarmTime() != null && secondary.getAlarmTime().isBefore(primary.getAlarmTime()))) {
            primary.setAlarmTime(secondary.getAlarmTime());
        }

        if (secondary.getDescription() != null) {
            String mergedDesc = primary.getDescription() != null
                    ? primary.getDescription() + " | " + secondary.getDescription()
                    : secondary.getDescription();
            primary.setDescription(mergedDesc);
        }

        primary.setUpdatedAt(LocalDateTime.now());
        return primary;
    }

    public enum AggregationType {
        REGION,
        SEVERITY,
        TIME_WINDOW
    }
}
