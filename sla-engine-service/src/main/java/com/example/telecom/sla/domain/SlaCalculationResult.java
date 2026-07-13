package com.example.telecom.sla.domain;

import java.util.Map;

public class SlaCalculationResult {

    private final String calculatorType;
    private final double metricValue;
    private final double threshold;
    private final boolean passed;
    private final Map<String, Object> details;

    public SlaCalculationResult(String calculatorType, double metricValue,
                                double threshold, boolean passed, Map<String, Object> details) {
        this.calculatorType = calculatorType;
        this.metricValue = metricValue;
        this.threshold = threshold;
        this.passed = passed;
        this.details = details;
    }

    public String getCalculatorType() { return calculatorType; }
    public double getMetricValue() { return metricValue; }
    public double getThreshold() { return threshold; }
    public boolean isPassed() { return passed; }
    public Map<String, Object> getDetails() { return details; }

    @Override
    public String toString() {
        return "SlaCalculationResult{" +
                "calculatorType='" + calculatorType + '\'' +
                ", metricValue=" + metricValue +
                ", passed=" + passed +
                '}';
    }
}
