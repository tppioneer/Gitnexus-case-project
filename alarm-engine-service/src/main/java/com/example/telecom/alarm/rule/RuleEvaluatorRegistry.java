package com.example.telecom.alarm.rule;

import com.example.telecom.common.device.MetricType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registry that resolves the correct RuleEvaluator based on metric type.
 * Called by AlarmEvaluationService through the RuleEvaluator interface.
 */
public class RuleEvaluatorRegistry {

    private final Map<MetricType, RuleEvaluator> evaluatorMap;

    public RuleEvaluatorRegistry(List<RuleEvaluator> evaluators) {
        this.evaluatorMap = evaluators.stream()
                .collect(Collectors.toMap(
                        e -> MetricType.valueOf(e.getClass().getSimpleName()
                                .replace("RuleEvaluator", "")
                                .replaceAll("([a-z])([A-Z])", "$1_$2")
                                .toUpperCase()),
                        e -> e
                ));
    }

    public RuleEvaluator resolve(MetricType metricType) {
        RuleEvaluator evaluator = evaluatorMap.get(metricType);
        if (evaluator == null) {
            throw new IllegalArgumentException("No RuleEvaluator found for metric type: " + metricType);
        }
        return evaluator;
    }
}
