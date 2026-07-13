package com.example.telecom.dispatch.rule;

import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadBalanceRule implements DispatchRule {

    private final int priority;
    private final String name;
    private final Map<String, Integer> operatorLoads;

    public LoadBalanceRule() {
        this.priority = 7;
        this.name = "LoadBalanceRule";
        this.operatorLoads = new HashMap<>();
    }

    public void setOperatorLoad(String operatorId, int load) {
        operatorLoads.put(operatorId, load);
    }

    @Override
    public DispatchRuleResult evaluate(DispatchContext context) {
        List<String> availableOperators = context.getAvailableOperators();
        if (availableOperators == null || availableOperators.isEmpty()) {
            return new DispatchRuleResult(name, 0.0, false, "No available operators to evaluate load");
        }

        int totalLoad = 0;
        int operatorCount = 0;

        for (String operatorId : availableOperators) {
            int load = operatorLoads.getOrDefault(operatorId, 0);
            totalLoad += load;
            operatorCount++;
        }

        if (operatorCount == 0) {
            return new DispatchRuleResult(name, 0.0, false, "No operators to evaluate");
        }

        double averageLoad = (double) totalLoad / operatorCount;
        double score = Math.max(0, 1.0 - (averageLoad / 10.0));
        boolean passed = averageLoad < 5;

        return new DispatchRuleResult(name, score, passed,
                String.format("Average operator load: %.1f (max allowed: 5)", averageLoad));
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }
}
