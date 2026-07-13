package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmStatus;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FederatedAlarmDeduplicationService {

    private final FederatedAlarmRepository alarmRepository;

    private static final double SIMILARITY_THRESHOLD = 0.85;
    private static final Duration DEDUP_WINDOW = Duration.ofMinutes(5);

    public FederatedAlarmDeduplicationService(FederatedAlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public List<FederatedAlarmRecord> deduplicate(List<FederatedAlarmRecord> alarms) {
        if (alarms == null || alarms.isEmpty()) {
            return Collections.emptyList();
        }

        List<FederatedAlarmRecord> unique = new ArrayList<>();
        for (FederatedAlarmRecord alarm : alarms) {
            boolean isDuplicate = unique.stream()
                    .anyMatch(existing -> calculateSimilarity(existing, alarm) >= SIMILARITY_THRESHOLD);
            if (!isDuplicate) {
                unique.add(alarm);
            } else {
                alarm.setStatus(FederatedAlarmStatus.DEDUPLICATED);
                alarmRepository.save(alarm);
            }
        }
        return unique;
    }

    public boolean isDuplicate(FederatedAlarmRecord alarm) {
        return alarmRepository.findAll().stream()
                .filter(r -> !r.getAlarmId().equals(alarm.getAlarmId()))
                .filter(r -> r.getStatus() != FederatedAlarmStatus.RESOLVED
                        && r.getStatus() != FederatedAlarmStatus.CLOSED)
                .anyMatch(existing -> calculateSimilarity(existing, alarm) >= SIMILARITY_THRESHOLD);
    }

    public List<FederatedAlarmRecord> findDuplicates(FederatedAlarmRecord alarm) {
        return alarmRepository.findAll().stream()
                .filter(r -> !r.getAlarmId().equals(alarm.getAlarmId()))
                .filter(r -> calculateSimilarity(r, alarm) >= SIMILARITY_THRESHOLD)
                .collect(Collectors.toList());
    }

    public String getDeduplicationKey(FederatedAlarmRecord alarm) {
        String source = alarm.getSourceId() != null ? alarm.getSourceId() : "NULL";
        String device = alarm.getDeviceId() != null ? alarm.getDeviceId() : "NULL";
        String type = alarm.getAlarmType() != null ? alarm.getAlarmType() : "NULL";
        String severity = alarm.getSeverity() != null ? alarm.getSeverity().name() : "NULL";
        return source + "::" + device + "::" + type + "::" + severity;
    }

    private double calculateSimilarity(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        double score = 0.0;

        if (Objects.equals(a.getSourceId(), b.getSourceId())) {
            score += 0.25;
        }
        if (Objects.equals(a.getDeviceId(), b.getDeviceId())) {
            score += 0.30;
        }
        if (Objects.equals(a.getAlarmType(), b.getAlarmType())) {
            score += 0.20;
        }
        if (a.getSeverity() != null && a.getSeverity() == b.getSeverity()) {
            score += 0.10;
        }
        if (a.getAlarmTime() != null && b.getAlarmTime() != null) {
            Duration diff = Duration.between(a.getAlarmTime(), b.getAlarmTime()).abs();
            if (diff.compareTo(DEDUP_WINDOW) <= 0) {
                score += 0.15;
            }
        }

        return score;
    }
}
