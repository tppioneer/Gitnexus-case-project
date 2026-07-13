package com.example.telecom.sla.calculator;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.domain.SlaCalculationResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SlaTimeCalculator implements SlaCalculator {

    @Override
    public SlaCalculationResult calculate(SlaContract contract, List<SlaMetricSnapshot> metrics) {
        double responseTime = calculateResponseTime(metrics);
        double resolutionTime = calculateResolutionTime(metrics);
        boolean passed = responseTime <= contract.getResponseTimeThreshold()
                && resolutionTime <= contract.getResolutionTimeThreshold();

        Map<String, Object> details = new HashMap<>();
        details.put("responseTime", responseTime);
        details.put("resolutionTime", resolutionTime);
        details.put("responseTimeThreshold", contract.getResponseTimeThreshold());
        details.put("resolutionTimeThreshold", contract.getResolutionTimeThreshold());

        return new SlaCalculationResult("time", Math.max(responseTime, resolutionTime),
                Math.max(contract.getResponseTimeThreshold(), contract.getResolutionTimeThreshold()),
                passed, details);
    }

    public double calculateResponseTime(List<SlaMetricSnapshot> metrics) {
        List<SlaMetricSnapshot> responseMetrics = metrics.stream()
                .filter(m -> "response_time".equals(m.getMetricType()))
                .collect(Collectors.toList());

        if (responseMetrics.isEmpty()) {
            return 0.0;
        }

        return responseMetrics.stream()
                .mapToDouble(SlaMetricSnapshot::getMetricValue)
                .average()
                .orElse(0.0);
    }

    public double calculateResolutionTime(List<SlaMetricSnapshot> metrics) {
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
}
