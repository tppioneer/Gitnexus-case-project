package com.example.telecom.sla.monitor;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.calculator.SlaCalculatorRegistry;
import com.example.telecom.sla.domain.SlaCalculationResult;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.repository.SlaMetricRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SlaMonitor {

    private final SlaContractRepository contractRepository;
    private final SlaMetricRepository metricRepository;
    private final SlaBreachPolicy breachPolicy;
    private final SlaCalculatorRegistry calculatorRegistry;

    public SlaMonitor(SlaContractRepository contractRepository,
                      SlaMetricRepository metricRepository,
                      SlaBreachPolicy breachPolicy,
                      SlaCalculatorRegistry calculatorRegistry) {
        this.contractRepository = contractRepository;
        this.metricRepository = metricRepository;
        this.breachPolicy = breachPolicy;
        this.calculatorRegistry = calculatorRegistry;
    }

    public SlaMonitorResult monitor(String contractId) {
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new IllegalArgumentException("Contract not found: " + contractId);
        }

        List<SlaMetricSnapshot> metrics = collectMetrics(contractId);
        List<SlaCalculationResult> evaluationResults = evaluateMetrics(contractId, metrics);
        List<String> breaches = new ArrayList<>();
        Map<String, SeverityInfo> breachDetails = new LinkedHashMap<>();

        for (SlaMetricSnapshot metric : metrics) {
            if (breachPolicy.evaluate(contract, metric)) {
                breaches.add(metric.getMetricType());
                breachDetails.put(metric.getMetricType(), new SeverityInfo(
                        metric.getMetricValue(),
                        getThresholdForMetric(contract, metric),
                        breachPolicy.getBreachSeverity(contract, metric)
                ));
            }
        }

        Map<String, Object> trend = analyzeTrend(metrics);
        double overallCompliance = 0.0;
        if (!evaluationResults.isEmpty()) {
            long passed = evaluationResults.stream().filter(SlaCalculationResult::isPassed).count();
            overallCompliance = (double) passed / evaluationResults.size() * 100.0;
        }

        return new SlaMonitorResult(
                contractId, contract.getContractName(), metrics.size(),
                evaluationResults, breaches.size(), breaches, breachDetails,
                trend, overallCompliance, LocalDateTime.now()
        );
    }

    public List<SlaMetricSnapshot> collectMetrics(String contractId) {
        return metricRepository.findByContractId(contractId);
    }

    public List<SlaCalculationResult> evaluateMetrics(String contractId, List<SlaMetricSnapshot> metrics) {
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            return new ArrayList<>();
        }
        return calculatorRegistry.calculateAll(contract, metrics);
    }

    public List<String> triggerBreachCheck(String contractId) {
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            return new ArrayList<>();
        }

        List<SlaMetricSnapshot> metrics = collectMetrics(contractId);
        List<String> breachedMetrics = new ArrayList<>();

        for (SlaMetricSnapshot metric : metrics) {
            if (breachPolicy.evaluate(contract, metric)) {
                breachedMetrics.add(metric.getMetricType());
            }
        }

        return breachedMetrics;
    }

    public SlaMonitorResult monitorWithDetails(String contractId) {
        return monitor(contractId);
    }

    private Map<String, Object> analyzeTrend(List<SlaMetricSnapshot> metrics) {
        Map<String, Object> trend = new LinkedHashMap<>();
        if (metrics.size() < 2) {
            trend.put("direction", "insufficient_data");
            trend.put("change", 0.0);
            trend.put("dataPoints", metrics.size());
            return trend;
        }

        List<SlaMetricSnapshot> sorted = metrics.stream()
                .sorted(Comparator.comparing(SlaMetricSnapshot::getTimestamp))
                .collect(Collectors.toList());

        double firstValue = sorted.get(0).getMetricValue();
        double lastValue = sorted.get(sorted.size() - 1).getMetricValue();
        double change = lastValue - firstValue;

        Map<String, Double> intervalChanges = new LinkedHashMap<>();
        for (int i = 1; i < sorted.size(); i++) {
            double intervalChange = sorted.get(i).getMetricValue() - sorted.get(i - 1).getMetricValue();
            intervalChanges.put(sorted.get(i).getTimestamp().toString(), intervalChange);
        }

        trend.put("direction", change > 0 ? "increasing" : change < 0 ? "decreasing" : "stable");
        trend.put("change", change);
        trend.put("percentageChange", firstValue != 0 ? (change / firstValue) * 100.0 : 0.0);
        trend.put("dataPoints", sorted.size());
        trend.put("intervalChanges", intervalChanges);
        trend.put("firstValue", firstValue);
        trend.put("lastValue", lastValue);
        trend.put("analyzedAt", LocalDateTime.now().toString());

        return trend;
    }

    private double getThresholdForMetric(SlaContract contract, SlaMetricSnapshot snapshot) {
        switch (snapshot.getMetricType()) {
            case "response_time":
                return contract.getResponseTimeThreshold();
            case "resolution_time":
                return contract.getResolutionTimeThreshold();
            case "availability":
                return contract.getAvailabilityTarget();
            default:
                return snapshot.getThreshold();
        }
    }

    public static class SeverityInfo {
        private final double actualValue;
        private final double threshold;
        private final String severity;

        public SeverityInfo(double actualValue, double threshold,
                            com.example.telecom.common.alarm.Severity severity) {
            this.actualValue = actualValue;
            this.threshold = threshold;
            this.severity = severity != null ? severity.name() : "UNKNOWN";
        }

        public double getActualValue() { return actualValue; }
        public double getThreshold() { return threshold; }
        public String getSeverity() { return severity; }
    }

    public static class SlaMonitorResult {
        private final String contractId;
        private final String contractName;
        private final int totalMetrics;
        private final List<SlaCalculationResult> evaluationResults;
        private final int breachCount;
        private final List<String> breachedMetrics;
        private final Map<String, SeverityInfo> breachDetails;
        private final Map<String, Object> trend;
        private final double overallCompliance;
        private final LocalDateTime monitoredAt;

        public SlaMonitorResult(String contractId, String contractName, int totalMetrics,
                                List<SlaCalculationResult> evaluationResults,
                                int breachCount, List<String> breachedMetrics,
                                Map<String, SeverityInfo> breachDetails,
                                Map<String, Object> trend,
                                double overallCompliance, LocalDateTime monitoredAt) {
            this.contractId = contractId;
            this.contractName = contractName;
            this.totalMetrics = totalMetrics;
            this.evaluationResults = evaluationResults;
            this.breachCount = breachCount;
            this.breachedMetrics = breachedMetrics;
            this.breachDetails = breachDetails;
            this.trend = trend;
            this.overallCompliance = overallCompliance;
            this.monitoredAt = monitoredAt;
        }

        public String getContractId() { return contractId; }
        public String getContractName() { return contractName; }
        public int getTotalMetrics() { return totalMetrics; }
        public List<SlaCalculationResult> getEvaluationResults() { return evaluationResults; }
        public int getBreachCount() { return breachCount; }
        public List<String> getBreachedMetrics() { return breachedMetrics; }
        public Map<String, SeverityInfo> getBreachDetails() { return breachDetails; }
        public Map<String, Object> getTrend() { return trend; }
        public double getOverallCompliance() { return overallCompliance; }
        public LocalDateTime getMonitoredAt() { return monitoredAt; }
    }
}
