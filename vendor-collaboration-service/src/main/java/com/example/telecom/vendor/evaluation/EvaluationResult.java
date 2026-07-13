package com.example.telecom.vendor.evaluation;

import java.util.LinkedHashMap;
import java.util.Map;

public class EvaluationResult {

    private final String evaluatorType;
    private final double score;
    private final String description;
    private final Map<String, Object> metrics;

    public EvaluationResult(String evaluatorType, double score, String description) {
        this.evaluatorType = evaluatorType;
        this.score = score;
        this.description = description;
        this.metrics = new LinkedHashMap<>();
    }

    public EvaluationResult(String evaluatorType, double score, String description, Map<String, Object> metrics) {
        this.evaluatorType = evaluatorType;
        this.score = score;
        this.description = description;
        this.metrics = new LinkedHashMap<>(metrics);
    }

    public String getEvaluatorType() { return evaluatorType; }
    public double getScore() { return score; }
    public String getDescription() { return description; }
    public Map<String, Object> getMetrics() { return metrics; }

    public void addMetric(String key, Object value) {
        this.metrics.put(key, value);
    }
}
