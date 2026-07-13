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
public class SlaResponseTimeCalculator implements SlaCalculator {

    @Override
    public SlaCalculationResult calculate(SlaContract contract, List<SlaMetricSnapshot> metrics) {
        double averageResponseTime = calculateAverage(metrics);
        double percentile95 = calculatePercentile(metrics, 95);
        double percentile99 = calculatePercentile(metrics, 99);
        int threshold = contract.getResponseTimeThreshold();
        boolean passed = averageResponseTime <= threshold;

        Map<String, Object> details = new HashMap<>();
        details.put("average", averageResponseTime);
        details.put("p95", percentile95);
        details.put("p99", percentile99);
        details.put("threshold", threshold);
        details.put("metricCount", metrics.size());

        return new SlaCalculationResult("response_time", averageResponseTime, threshold, passed, details);
    }

    public double calculatePercentile(List<SlaMetricSnapshot> metrics, int percentile) {
        List<SlaMetricSnapshot> responseMetrics = metrics.stream()
                .filter(m -> "response_time".equals(m.getMetricType()))
                .sorted((a, b) -> Double.compare(a.getMetricValue(), b.getMetricValue()))
                .collect(Collectors.toList());

        if (responseMetrics.isEmpty()) {
            return 0.0;
        }

        int index = (int) Math.ceil(percentile / 100.0 * responseMetrics.size()) - 1;
        index = Math.max(0, Math.min(index, responseMetrics.size() - 1));
        return responseMetrics.get(index).getMetricValue();
    }

    public double calculateAverage(List<SlaMetricSnapshot> metrics) {
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
}
