package com.example.telecom.sla.calculator;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.sla.domain.SlaCalculationResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SlaAvailabilityCalculator implements SlaCalculator {

    @Override
    public SlaCalculationResult calculate(SlaContract contract, List<SlaMetricSnapshot> metrics) {
        double availability = calculateUptime(metrics);
        double target = contract.getAvailabilityTarget();
        boolean passed = availability >= target;

        Map<String, Object> details = new HashMap<>();
        details.put("availability", availability);
        details.put("target", target);
        details.put("uptime", calculateUptime(metrics));
        details.put("downtime", calculateDowntime(metrics));

        return new SlaCalculationResult("availability", availability, target, passed, details);
    }

    public double calculateUptime(List<SlaMetricSnapshot> metrics) {
        List<SlaMetricSnapshot> availabilityMetrics = metrics.stream()
                .filter(m -> "availability".equals(m.getMetricType()))
                .collect(Collectors.toList());

        if (availabilityMetrics.isEmpty()) {
            return 100.0;
        }

        double totalUptime = availabilityMetrics.stream()
                .filter(m -> m.getMetricValue() > 0)
                .mapToDouble(SlaMetricSnapshot::getMetricValue)
                .sum();

        double totalTime = availabilityMetrics.stream()
                .mapToDouble(SlaMetricSnapshot::getMetricValue)
                .sum();

        if (totalTime == 0) {
            return 100.0;
        }

        return (totalUptime / totalTime) * 100.0;
    }

    public double calculateDowntime(List<SlaMetricSnapshot> metrics) {
        List<SlaMetricSnapshot> availabilityMetrics = metrics.stream()
                .filter(m -> "availability".equals(m.getMetricType()))
                .collect(Collectors.toList());

        if (availabilityMetrics.isEmpty()) {
            return 0.0;
        }

        return availabilityMetrics.stream()
                .filter(m -> m.getMetricValue() == 0)
                .mapToDouble(SlaMetricSnapshot::getMetricValue)
                .count();
    }
}
