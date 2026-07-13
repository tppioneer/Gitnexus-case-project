package com.example.telecom.alarm.federated.correlation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmStatus;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FederatedCorrelationEngine {

    private final FederatedAlarmRepository alarmRepository;
    private final CorrelationRuleMatcher ruleMatcher;

    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.8;
    private static final double MEDIUM_CONFIDENCE_THRESHOLD = 0.5;
    private static final Duration DEFAULT_CORRELATION_WINDOW = Duration.ofMinutes(30);

    public FederatedCorrelationEngine(FederatedAlarmRepository alarmRepository, CorrelationRuleMatcher ruleMatcher) {
        this.alarmRepository = alarmRepository;
        this.ruleMatcher = ruleMatcher;
    }

    public List<FederatedAlarmRecord> correlate(FederatedAlarmRecord alarm) {
        List<FederatedAlarmRecord> candidates = findCorrelationCandidates(alarm);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        List<FederatedAlarmRecord> correlated = new ArrayList<>();
        for (FederatedAlarmRecord candidate : candidates) {
            double confidence = getCorrelationConfidence(alarm, candidate);
            if (confidence >= MEDIUM_CONFIDENCE_THRESHOLD) {
                correlated.add(candidate);
                if (confidence >= HIGH_CONFIDENCE_THRESHOLD) {
                    String groupId = candidate.getCorrelationGroupId() != null
                            ? candidate.getCorrelationGroupId()
                            : UUID.randomUUID().toString();
                    alarm.setCorrelationGroupId(groupId);
                    candidate.setCorrelationGroupId(groupId);
                    alarm.setStatus(FederatedAlarmStatus.CORRELATED);
                    candidate.setStatus(FederatedAlarmStatus.CORRELATED);
                    alarmRepository.save(alarm);
                    alarmRepository.save(candidate);
                }
            }
        }
        return correlated;
    }

    public List<FederatedAlarmRecord> findCorrelationCandidates(FederatedAlarmRecord alarm) {
        return alarmRepository.findAll().stream()
                .filter(r -> !r.getAlarmId().equals(alarm.getAlarmId()))
                .filter(r -> r.getStatus() != FederatedAlarmStatus.RESOLVED
                        && r.getStatus() != FederatedAlarmStatus.CLOSED)
                .filter(r -> Duration.between(r.getAlarmTime(), alarm.getAlarmTime()).abs()
                        .compareTo(DEFAULT_CORRELATION_WINDOW) <= 0)
                .collect(Collectors.toList());
    }

    public Map<String, List<FederatedAlarmRecord>> buildCorrelationGraph(List<FederatedAlarmRecord> alarms) {
        Map<String, List<FederatedAlarmRecord>> graph = new LinkedHashMap<>();
        for (FederatedAlarmRecord alarm : alarms) {
            String groupId = alarm.getCorrelationGroupId() != null
                    ? alarm.getCorrelationGroupId()
                    : "UNGROUPED_" + alarm.getAlarmId();
            graph.computeIfAbsent(groupId, k -> new ArrayList<>()).add(alarm);
        }
        return graph;
    }

    public double getCorrelationConfidence(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        double score = 0.0;
        int rulesApplied = 0;

        if (ruleMatcher.matchByDeviceId(a, b)) {
            score += 0.4;
            rulesApplied++;
        }
        if (ruleMatcher.matchByTimeWindow(a, b, DEFAULT_CORRELATION_WINDOW)) {
            score += 0.2;
            rulesApplied++;
        }
        if (ruleMatcher.matchByAlarmType(a, b)) {
            score += 0.3;
            rulesApplied++;
        }
        if (a.getRegionCode() != null && a.getRegionCode().equals(b.getRegionCode())) {
            score += 0.1;
            rulesApplied++;
        }

        return rulesApplied > 0 ? score / Math.min(rulesApplied, 1) : 0.0;
    }

    public FederatedAlarmRecord mergeCorrelatedAlarms(List<FederatedAlarmRecord> group) {
        if (group == null || group.isEmpty()) {
            throw new IllegalArgumentException("Cannot merge an empty group");
        }

        FederatedAlarmRecord merged = new FederatedAlarmRecord();
        merged.setAlarmId(UUID.randomUUID().toString());
        merged.setStatus(FederatedAlarmStatus.CORRELATED);

        String groupId = group.stream()
                .map(FederatedAlarmRecord::getCorrelationGroupId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(UUID.randomUUID().toString());
        merged.setCorrelationGroupId(groupId);

        FederatedAlarmRecord primary = group.get(0);
        merged.setSourceId(primary.getSourceId());
        merged.setDeviceId(primary.getDeviceId());
        merged.setAlarmType(primary.getAlarmType());
        merged.setRegionCode(primary.getRegionCode());

        SeverityAggregator severityAggregator = new SeverityAggregator();
        group.forEach(a -> severityAggregator.add(a.getSeverity()));
        merged.setSeverity(severityAggregator.getHighestSeverity());

        LocalDateTime earliest = group.stream()
                .map(FederatedAlarmRecord::getAlarmTime)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
        merged.setAlarmTime(earliest);

        String mergedDesc = group.stream()
                .map(FederatedAlarmRecord::getDescription)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        merged.setDescription(mergedDesc);

        return merged;
    }

    private static class SeverityAggregator {
        private final List<com.example.telecom.common.alarm.Severity> severities = new ArrayList<>();

        void add(com.example.telecom.common.alarm.Severity severity) {
            severities.add(severity);
        }

        com.example.telecom.common.alarm.Severity getHighestSeverity() {
            return severities.stream()
                    .min(Comparator.comparingInt(Enum::ordinal))
                    .orElse(com.example.telecom.common.alarm.Severity.INFO);
        }
    }
}
