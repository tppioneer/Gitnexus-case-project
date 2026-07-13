package com.example.telecom.vendor.evaluation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VendorEvaluatorRegistry {

    private final Map<String, VendorEvaluator> evaluators = new ConcurrentHashMap<>();

    public void register(VendorEvaluator evaluator) {
        evaluators.put(evaluator.getEvaluatorType(), evaluator);
    }

    public Optional<VendorEvaluator> resolve(String evaluatorType) {
        if (evaluatorType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(evaluators.get(evaluatorType));
    }

    public List<VendorEvaluator> getAllEvaluators() {
        return new ArrayList<>(evaluators.values());
    }

    public List<EvaluationResult> evaluateAll(String vendorId) {
        return evaluators.values().stream()
                .map(e -> e.evaluate(vendorId))
                .collect(Collectors.toList());
    }
}
