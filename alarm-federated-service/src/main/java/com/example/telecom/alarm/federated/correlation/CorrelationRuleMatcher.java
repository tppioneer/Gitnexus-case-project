package com.example.telecom.alarm.federated.correlation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class CorrelationRuleMatcher {

    public boolean match(FederatedAlarmRecord alarm, java.util.List<FederatedAlarmRecord> candidates) {
        return candidates.stream().anyMatch(c -> getMatchScore(alarm, c) >= 0.5);
    }

    public boolean matchByDeviceId(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        if (a.getDeviceId() == null || b.getDeviceId() == null) {
            return false;
        }
        return a.getDeviceId().equals(b.getDeviceId());
    }

    public boolean matchByTimeWindow(FederatedAlarmRecord a, FederatedAlarmRecord b, Duration window) {
        if (a.getAlarmTime() == null || b.getAlarmTime() == null) {
            return false;
        }
        Duration diff = Duration.between(a.getAlarmTime(), b.getAlarmTime()).abs();
        return diff.compareTo(window) <= 0;
    }

    public boolean matchByAlarmType(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        if (a.getAlarmType() == null || b.getAlarmType() == null) {
            return false;
        }
        return a.getAlarmType().equals(b.getAlarmType());
    }

    public double getMatchScore(FederatedAlarmRecord a, FederatedAlarmRecord b) {
        double score = 0.0;
        int factors = 0;

        if (matchByDeviceId(a, b)) {
            score += 0.35;
            factors++;
        }

        if (matchByAlarmType(a, b)) {
            score += 0.25;
            factors++;
        }

        Duration window = Duration.ofMinutes(30);
        if (matchByTimeWindow(a, b, window)) {
            score += 0.20;
            factors++;
        }

        if (Objects.equals(a.getRegionCode(), b.getRegionCode())) {
            score += 0.10;
            factors++;
        }

        if (a.getSeverity() != null && b.getSeverity() != null
                && a.getSeverity().ordinal() <= b.getSeverity().ordinal()) {
            score += 0.10;
            factors++;
        }

        return factors > 0 ? score / factors : 0.0;
    }
}
