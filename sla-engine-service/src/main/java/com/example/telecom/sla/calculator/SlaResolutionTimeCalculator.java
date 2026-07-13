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
public class SlaResolutionTimeCalculator implements SlaCalculator {

    @Override
    public SlaCalculationResult calculate(SlaContract contract, List<SlaMetricSnapshot> metrics) {
        double mttr = calculateMTTR(metrics);
        double mtbf = calculateMTBF(metrics);
        int threshold = contract.getResolutionTimeThreshold();
        boolean passed = mttr <= threshold;

        Map<String, Object> details = new HashMap<>();
        details.put("mttr", mttr);
        details.put("mtbf", mtbf);
        details.put("threshold", threshold);
        details.put("metricCount", metrics.size());

        return new SlaCalculationResult("resolution_time", mttr, threshold, passed, details);
    }

    public double calculateMTTR(List<SlaMetricSnapshot> metrics) {
        List<SlaMetricSnapshot> resolutionMetrics = metrics.stream()
                .filter(m -> "resolution_time".equals(m.getMetricType()))
                .collect(Collectors.toList());

        if (resolutionMetrics.isEmpty()) {
            return 0.0;
        }

        return resolutionMetrics.stream()
                .mapToDouble(SlaMetricSnapshot::getMetricValue)
                .average()
                .orElse(0.0);
    }

    public double calculateMTBF(List<SlaMetricSnapshot> metrics) {
        List<SlaMetricSnapshot> resolutionMetrics = metrics.stream()
                .filter(m -> "resolution_time".equals(m.getMetricType()))
                .collect(Collectors.toList());

        if (resolutionMetrics.size() < 2) {
            return 0.0;
        }

        double totalInterval = 0;
        for (int i = 1; i < resolutionMetrics.size(); i++) {
            totalInterval += Math.abs(resolutionMetrics.get(i).getMetricValue()
                    - resolutionMetrics.get(i - 1).getMetricValue());
        }

        return totalInterval / (resolutionMetrics.size() - 1);
    }
}
