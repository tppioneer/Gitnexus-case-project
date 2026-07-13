package com.example.telecom.dispatch.rule;

import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;

import java.util.*;

public class ProximityRule implements DispatchRule {

    private final int priority;
    private final String name;
    private final Map<String, String> operatorRegions;
    private final Map<String, List<String>> adjacencyMap;

    public ProximityRule() {
        this.priority = 5;
        this.name = "ProximityRule";
        this.operatorRegions = new HashMap<>();
        this.adjacencyMap = new HashMap<>();
        initializeAdjacency();
    }

    private void initializeAdjacency() {
        adjacencyMap.put("REGION-A", Arrays.asList("REGION-B", "REGION-C"));
        adjacencyMap.put("REGION-B", Arrays.asList("REGION-A", "REGION-D"));
        adjacencyMap.put("REGION-C", Arrays.asList("REGION-A", "REGION-D"));
        adjacencyMap.put("REGION-D", Arrays.asList("REGION-B", "REGION-C"));
    }

    public void registerOperatorRegion(String operatorId, String regionCode) {
        operatorRegions.put(operatorId, regionCode);
    }

    @Override
    public DispatchRuleResult evaluate(DispatchContext context) {
        String targetRegion = context.getRegionCode();
        if (targetRegion == null || targetRegion.isEmpty()) {
            return new DispatchRuleResult(name, 1.0, true, "No region specified");
        }

        List<String> availableOperators = context.getAvailableOperators();
        if (availableOperators == null || availableOperators.isEmpty()) {
            return new DispatchRuleResult(name, 0.0, false, "No available operators");
        }

        double totalProximity = 0.0;
        int operatorCount = 0;

        for (String operatorId : availableOperators) {
            String operatorRegion = operatorRegions.get(operatorId);
            if (operatorRegion != null) {
                double proximity = calculateProximity(operatorRegion, targetRegion);
                totalProximity += proximity;
                operatorCount++;
            }
        }

        if (operatorCount == 0) {
            return new DispatchRuleResult(name, 0.0, false, "No operators have registered regions");
        }

        double averageProximity = totalProximity / operatorCount;
        boolean passed = averageProximity > 0.3;

        return new DispatchRuleResult(name, averageProximity, passed,
                String.format("Average proximity score: %.2f across %d operators", averageProximity, operatorCount));
    }

    private double calculateProximity(String operatorRegion, String targetRegion) {
        if (operatorRegion.equals(targetRegion)) {
            return 1.0;
        }
        List<String> adjacent = adjacencyMap.get(targetRegion);
        if (adjacent != null && adjacent.contains(operatorRegion)) {
            return 0.5;
        }
        return 0.2;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }
}
