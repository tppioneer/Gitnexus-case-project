package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmStatus;
import com.example.telecom.alarm.federated.correlation.CorrelationRuleMatcher;
import com.example.telecom.alarm.federated.correlation.FederatedCorrelationEngine;
import com.example.telecom.alarm.federated.event.AlarmFederationEventPublisher;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FederatedAlarmCorrelationService {

    private final FederatedAlarmRepository alarmRepository;
    private final FederatedCorrelationEngine correlationEngine;
    private final CorrelationRuleMatcher ruleMatcher;
    private final AlarmFederationEventPublisher eventPublisher;

    public FederatedAlarmCorrelationService(FederatedAlarmRepository alarmRepository,
                                             FederatedCorrelationEngine correlationEngine,
                                             CorrelationRuleMatcher ruleMatcher,
                                             AlarmFederationEventPublisher eventPublisher) {
        this.alarmRepository = alarmRepository;
        this.correlationEngine = correlationEngine;
        this.ruleMatcher = ruleMatcher;
        this.eventPublisher = eventPublisher;
    }

    public List<FederatedAlarmRecord> correlate(FederatedAlarmRecord alarm) {
        List<FederatedAlarmRecord> correlated = correlationEngine.correlate(alarm);
        if (!correlated.isEmpty()) {
            String groupId = correlated.get(0).getCorrelationGroupId();
            eventPublisher.publishAlarmCorrelated(alarm, groupId);
        }
        return correlated;
    }

    public List<FederatedAlarmRecord> findCorrelatedAlarms(String alarmId) {
        Optional<FederatedAlarmRecord> alarmOpt = alarmRepository.findById(alarmId);
        if (alarmOpt.isEmpty()) {
            return Collections.emptyList();
        }

        FederatedAlarmRecord alarm = alarmOpt.get();
        if (alarm.getCorrelationGroupId() == null) {
            return Collections.emptyList();
        }

        return alarmRepository.findAll().stream()
                .filter(r -> alarm.getCorrelationGroupId().equals(r.getCorrelationGroupId()))
                .filter(r -> !r.getAlarmId().equals(alarmId))
                .collect(Collectors.toList());
    }

    public List<FederatedAlarmRecord> buildCorrelationGroup(List<FederatedAlarmRecord> alarms) {
        if (alarms == null || alarms.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<FederatedAlarmRecord>> graph = correlationEngine.buildCorrelationGraph(alarms);
        List<FederatedAlarmRecord> merged = new ArrayList<>();

        for (Map.Entry<String, List<FederatedAlarmRecord>> entry : graph.entrySet()) {
            if (entry.getKey().startsWith("UNGROUPED_")) {
                merged.addAll(entry.getValue());
            } else {
                FederatedAlarmRecord mergedAlarm = correlationEngine.mergeCorrelatedAlarms(entry.getValue());
                merged.add(mergedAlarm);
            }
        }

        return merged;
    }

    public double getCorrelationScore(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        return ruleMatcher.getMatchScore(a, b);
    }

    public boolean matchBySource(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        if (a.getSourceId() == null || b.getSourceId() == null) {
            return false;
        }
        return a.getSourceId().equals(b.getSourceId());
    }

    public boolean matchByTimeWindow(FederatedAlarmRecord a, FederatedAlarmRecord b, Duration window) {
        return ruleMatcher.matchByTimeWindow(a, b, window);
    }
}
