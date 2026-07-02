package com.example.telecom.alarm.rule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Summarizes rule evaluation results across multiple metrics.
 */
public class RuleEvaluationSummary {

    private int totalEvaluations;
    private int triggeredEvaluations;
    private int normalEvaluations;
    private Map<String, Integer> triggeredByRule;
    private Map<String, Integer> triggeredByMetricType;
    private long windowStartMs;
    private long windowEndMs;

    public RuleEvaluationSummary() {
        this.triggeredByRule = new HashMap<>();
        this.triggeredByMetricType = new HashMap<>();
    }

    public void recordEvaluation(String ruleId, String metricType, boolean triggered) {
        totalEvaluations++;
        if (triggered) {
            triggeredEvaluations++;
            triggeredByRule.merge(ruleId, 1, Integer::sum);
            triggeredByMetricType.merge(metricType, 1, Integer::sum);
        } else {
            normalEvaluations++;
        }
    }

    public double getTriggerRate() {
        return totalEvaluations > 0 ? (double) triggeredEvaluations / totalEvaluations * 100.0 : 0.0;
    }

    public String getMostTriggeredRule() {
        return triggeredByRule.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NONE");
    }

    public String getMostTriggeredMetricType() {
        return triggeredByMetricType.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NONE");
    }

    public int getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(int totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    public int getTriggeredEvaluations() { return triggeredEvaluations; }
    public void setTriggeredEvaluations(int triggeredEvaluations) { this.triggeredEvaluations = triggeredEvaluations; }
    public int getNormalEvaluations() { return normalEvaluations; }
    public void setNormalEvaluations(int normalEvaluations) { this.normalEvaluations = normalEvaluations; }
    public Map<String, Integer> getTriggeredByRule() { return triggeredByRule; }
    public void setTriggeredByRule(Map<String, Integer> triggeredByRule) { this.triggeredByRule = triggeredByRule; }
    public Map<String, Integer> getTriggeredByMetricType() { return triggeredByMetricType; }
    public void setTriggeredByMetricType(Map<String, Integer> triggeredByMetricType) { this.triggeredByMetricType = triggeredByMetricType; }
    public long getWindowStartMs() { return windowStartMs; }
    public void setWindowStartMs(long windowStartMs) { this.windowStartMs = windowStartMs; }
    public long getWindowEndMs() { return windowEndMs; }
    public void setWindowEndMs(long windowEndMs) { this.windowEndMs = windowEndMs; }
}
