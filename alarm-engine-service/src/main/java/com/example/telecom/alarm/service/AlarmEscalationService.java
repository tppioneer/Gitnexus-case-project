package com.example.telecom.alarm.service;

import com.example.telecom.alarm.policy.AlarmEscalationPolicy;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;

public class AlarmEscalationService {

    private final List<AlarmEscalationPolicy> escalationPolicies;
    private final AlarmRepository alarmRepository;

    public AlarmEscalationService(List<AlarmEscalationPolicy> escalationPolicies,
                                   AlarmRepository alarmRepository) {
        this.escalationPolicies = escalationPolicies;
        this.alarmRepository = alarmRepository;
    }

    public List<AlarmRecord> findAlarmsToEscalate() {
        return alarmRepository.findActiveAlarms().stream()
                .filter(alarm -> escalationPolicies.stream().anyMatch(p -> p.shouldEscalate(alarm)))
                .toList();
    }

    public String evaluate(AlarmRecord alarm) {
        StringBuilder sb = new StringBuilder();
        for (AlarmEscalationPolicy policy : escalationPolicies) {
            sb.append(policy.evaluate(alarm)).append("; ");
        }
        return sb.toString().trim();
    }
}
