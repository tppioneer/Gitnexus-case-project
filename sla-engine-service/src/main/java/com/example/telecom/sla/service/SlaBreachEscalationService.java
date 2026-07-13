package com.example.telecom.sla.service;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.config.SlaProperties;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.event.SlaEventPublisher;
import com.example.telecom.sla.repository.SlaBreachRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlaBreachEscalationService {

    private static final Logger log = LoggerFactory.getLogger(SlaBreachEscalationService.class);

    private final SlaBreachRepository breachRepository;
    private final SlaEventPublisher eventPublisher;
    private final SlaProperties slaProperties;

    public SlaBreachEscalationService(SlaBreachRepository breachRepository,
                                      SlaEventPublisher eventPublisher,
                                      SlaProperties slaProperties) {
        this.breachRepository = breachRepository;
        this.eventPublisher = eventPublisher;
        this.slaProperties = slaProperties;
    }

    public SlaBreach escalate(String breachId) {
        log.info("Escalating SLA breach: breachId={}", breachId);
        SlaBreach breach = breachRepository.findById(breachId);
        if (breach == null) {
            log.warn("SLA breach not found for escalation: breachId={}", breachId);
            throw new DomainException("BREACH_NOT_FOUND", "SLA breach not found: " + breachId);
        }

        if (breach.getStatus() != VendorSlaStatus.BREACHED) {
            throw new DomainException("BREACH_NOT_ACTIVE",
                    "Cannot escalate a resolved breach: " + breachId);
        }

        EscalationLevel currentLevel = breach.getEscalatedTo() != null
                ? breach.getEscalatedTo() : EscalationLevel.NONE;
        EscalationLevel nextLevel = getNextLevel(currentLevel);

        if (nextLevel == currentLevel) {
            log.info("Breach {} already at max escalation level: {}", breachId, currentLevel);
            return breach;
        }

        breach.setEscalatedTo(nextLevel);
        breachRepository.save(breach);

        eventPublisher.publishEscalated(breach, nextLevel);
        log.warn("Breach escalated: breachId={}, contractId={}, from={}, to={}",
                breachId, breach.getContractId(), currentLevel, nextLevel);

        return breach;
    }

    public EscalationLevel determineEscalationLevel(SlaBreach breach) {
        Severity severity = breach.getSeverity();
        EscalationLevel level;

        switch (severity) {
            case CRITICAL:
                level = EscalationLevel.LEVEL_5;
                break;
            case MAJOR:
                level = EscalationLevel.LEVEL_3;
                break;
            case WARNING:
                level = EscalationLevel.LEVEL_2;
                break;
            default:
                level = EscalationLevel.LEVEL_1;
                break;
        }

        log.debug("Determined escalation level for breach {}: severity={}, level={}",
                breach.getBreachId(), severity, level);
        return level;
    }

    public void notifyEscalation(SlaBreach breach, EscalationLevel level) {
        log.info("Sending escalation notification: breachId={}, contractId={}, level={}, metricType={}",
                breach.getBreachId(), breach.getContractId(), level, breach.getMetricType());

        String notificationMessage = String.format(
                "ESCALATION: SLA Breach %s for contract %s on metric %s has been escalated to %s. " +
                        "Actual value: %.2f, Threshold: %.2f",
                breach.getBreachId(), breach.getContractId(), breach.getMetricType(),
                level, breach.getActualValue(), breach.getThreshold()
        );
        log.warn(notificationMessage);

        eventPublisher.publishEscalated(breach, level);
    }

    public List<SlaEscalationRecord> getEscalationHistory(String breachId) {
        log.debug("Fetching escalation history for breach: breachId={}", breachId);
        SlaBreach breach = breachRepository.findById(breachId);
        if (breach == null) {
            throw new DomainException("BREACH_NOT_FOUND", "SLA breach not found: " + breachId);
        }

        List<SlaEscalationRecord> history = new ArrayList<>();
        if (breach.getEscalatedTo() != null && breach.getEscalatedTo() != EscalationLevel.NONE) {
            history.add(new SlaEscalationRecord(
                    breach.getBreachId(),
                    breach.getEscalatedTo(),
                    breach.getDetectedTime(),
                    "Automated escalation based on severity: " + breach.getSeverity()
            ));
            history.add(new SlaEscalationRecord(
                    breach.getBreachId(),
                    EscalationLevel.NONE,
                    breach.getDetectedTime(),
                    "Initial breach detection"
            ));
        }
        return history;
    }

    public List<SlaBreach> autoEscalate() {
        log.info("Running auto-escalation check for all active breaches");
        List<SlaBreach> activeBreaches = breachRepository.findActiveBreaches();
        List<SlaBreach> escalated = new ArrayList<>();

        for (SlaBreach breach : activeBreaches) {
            try {
                Duration timeSinceDetection = Duration.between(breach.getDetectedTime(), LocalDateTime.now());
                EscalationLevel currentLevel = breach.getEscalatedTo() != null
                        ? breach.getEscalatedTo() : EscalationLevel.NONE;

                long minutesSinceDetection = timeSinceDetection.toMinutes();
                EscalationLevel targetLevel = determineLevelByDuration(minutesSinceDetection);

                if (targetLevel.ordinal() > currentLevel.ordinal()) {
                    breach.setEscalatedTo(targetLevel);
                    breachRepository.save(breach);
                    eventPublisher.publishEscalated(breach, targetLevel);
                    escalated.add(breach);
                    log.warn("Auto-escalated breach {} to level {} after {} minutes",
                            breach.getBreachId(), targetLevel, minutesSinceDetection);
                }
            } catch (Exception e) {
                log.error("Error auto-escalating breach: {}", breach.getBreachId(), e);
            }
        }

        if (escalated.isEmpty()) {
            log.info("No breaches required auto-escalation at this time");
        } else {
            log.info("Auto-escalated {} breaches", escalated.size());
        }

        return escalated;
    }

    public Map<String, Object> getEscalationSummary() {
        List<SlaBreach> activeBreaches = breachRepository.findActiveBreaches();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalActiveBreaches", activeBreaches.size());
        summary.put("notEscalated", activeBreaches.stream()
                .filter(b -> b.getEscalatedTo() == null || b.getEscalatedTo() == EscalationLevel.NONE)
                .count());
        summary.put("level1", activeBreaches.stream()
                .filter(b -> b.getEscalatedTo() == EscalationLevel.LEVEL_1).count());
        summary.put("level2", activeBreaches.stream()
                .filter(b -> b.getEscalatedTo() == EscalationLevel.LEVEL_2).count());
        summary.put("level3", activeBreaches.stream()
                .filter(b -> b.getEscalatedTo() == EscalationLevel.LEVEL_3).count());
        summary.put("critical", activeBreaches.stream()
                .filter(b -> b.getEscalatedTo() == EscalationLevel.LEVEL_5).count());
        summary.put("maxEscalationLevel", slaProperties.getMaxEscalationLevel());
        summary.put("lastChecked", LocalDateTime.now());
        return summary;
    }

    private EscalationLevel getNextLevel(EscalationLevel current) {
        int maxOrdinal = slaProperties.getMaxEscalationLevel();
        if (current.ordinal() >= maxOrdinal) {
            return current;
        }
        EscalationLevel[] levels = EscalationLevel.values();
        int nextIndex = Math.min(current.ordinal() + 1, levels.length - 1);
        nextIndex = Math.min(nextIndex, maxOrdinal);
        return levels[nextIndex];
    }

    private EscalationLevel determineLevelByDuration(long minutesSinceDetection) {
        if (minutesSinceDetection > 120) {
            return EscalationLevel.LEVEL_5;
        } else if (minutesSinceDetection > 60) {
            return EscalationLevel.LEVEL_3;
        } else if (minutesSinceDetection > 30) {
            return EscalationLevel.LEVEL_2;
        } else if (minutesSinceDetection > 15) {
            return EscalationLevel.LEVEL_1;
        }
        return EscalationLevel.NONE;
    }

    public static class SlaEscalationRecord {
        private final String breachId;
        private final EscalationLevel level;
        private final LocalDateTime escalatedAt;
        private final String reason;

        public SlaEscalationRecord(String breachId, EscalationLevel level,
                                   LocalDateTime escalatedAt, String reason) {
            this.breachId = breachId;
            this.level = level;
            this.escalatedAt = escalatedAt;
            this.reason = reason;
        }

        public String getBreachId() { return breachId; }
        public EscalationLevel getLevel() { return level; }
        public LocalDateTime getEscalatedAt() { return escalatedAt; }
        public String getReason() { return reason; }
    }
}
