package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmStatus;
import com.example.telecom.alarm.federated.event.AlarmFederationEventPublisher;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.common.alarm.Severity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FederatedAlarmEscalationService {

    private final FederatedAlarmRepository alarmRepository;
    private final AlarmFederationEventPublisher eventPublisher;

    private static final int MAX_ESCALATION_LEVEL = 5;
    private static final Duration ESCALATION_TIME_BASE = Duration.ofMinutes(15);

    public FederatedAlarmEscalationService(FederatedAlarmRepository alarmRepository,
                                            AlarmFederationEventPublisher eventPublisher) {
        this.alarmRepository = alarmRepository;
        this.eventPublisher = eventPublisher;
    }

    public FederatedAlarmRecord escalate(FederatedAlarmRecord alarm) {
        String level = getEscalationLevel(alarm);
        alarm.setEscalationLevel(alarm.getEscalationLevel() + 1);
        alarm.setStatus(FederatedAlarmStatus.ESCALATED);
        alarm.setUpdatedAt(LocalDateTime.now());

        FederatedAlarmRecord saved = alarmRepository.save(alarm);
        notifyEscalation(saved, level);
        eventPublisher.publishAlarmEscalated(saved, level);
        return saved;
    }

    public String getEscalationLevel(FederatedAlarmRecord alarm) {
        int currentLevel = alarm.getEscalationLevel();
        double severityScore = calculateSeverityScore(alarm);
        Duration age = Duration.between(alarm.getAlarmTime(), LocalDateTime.now());

        int calculatedLevel = currentLevel;

        if (severityScore >= 9.0 && age.toMinutes() > 5) {
            calculatedLevel = Math.max(calculatedLevel, 4);
        } else if (severityScore >= 7.0 && age.toMinutes() > 15) {
            calculatedLevel = Math.max(calculatedLevel, 3);
        } else if (severityScore >= 5.0 && age.toMinutes() > 30) {
            calculatedLevel = Math.max(calculatedLevel, 2);
        } else if (severityScore >= 3.0 && age.toHours() > 1) {
            calculatedLevel = Math.max(calculatedLevel, 1);
        }

        if (age.toHours() > 4) {
            calculatedLevel = Math.max(calculatedLevel, 3);
        }
        if (age.toHours() > 12) {
            calculatedLevel = Math.max(calculatedLevel, 4);
        }
        if (age.toHours() > 24) {
            calculatedLevel = Math.max(calculatedLevel, 5);
        }

        return "LEVEL_" + Math.min(calculatedLevel, MAX_ESCALATION_LEVEL);
    }

    public void notifyEscalation(FederatedAlarmRecord alarm, String level) {
        String message = String.format(
                "ESCALATION [%s]: Alarm %s from source %s has been escalated to %s. " +
                        "Severity: %s, Device: %s, Region: %s",
                level,
                alarm.getAlarmId(),
                alarm.getSourceId(),
                level,
                alarm.getSeverity(),
                alarm.getDeviceId(),
                alarm.getRegionCode()
        );
        // In a real system this would send email, SMS, or push notification
        System.out.println(message);
    }

    public List<FederatedAlarmRecord> autoEscalateIfNeeded() {
        List<FederatedAlarmRecord> activeAlarms = alarmRepository.findAll().stream()
                .filter(r -> r.getStatus() != FederatedAlarmStatus.RESOLVED
                        && r.getStatus() != FederatedAlarmStatus.CLOSED)
                .collect(Collectors.toList());

        return activeAlarms.stream()
                .filter(alarm -> {
                    String currentLevel = getEscalationLevel(alarm);
                    int currentLevelNum = Integer.parseInt(currentLevel.replace("LEVEL_", ""));
                    return currentLevelNum > alarm.getEscalationLevel();
                })
                .map(this::escalate)
                .collect(Collectors.toList());
    }

    private double calculateSeverityScore(FederatedAlarmRecord alarm) {
        if (alarm.getSeverity() == null) {
            return 0.0;
        }
        return switch (alarm.getSeverity()) {
            case CRITICAL -> 10.0;
            case MAJOR -> 8.0;
            case WARNING -> 3.0;
            case INFO -> 1.0;
        };
    }
}
