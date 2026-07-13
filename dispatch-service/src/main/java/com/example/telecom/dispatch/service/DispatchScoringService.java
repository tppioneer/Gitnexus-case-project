package com.example.telecom.dispatch.service;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.dispatch.domain.Operator;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DispatchScoringService {

    private final Map<String, List<String>> operatorSkills = new HashMap<>();
    private final Map<String, Integer> operatorLoads = new HashMap<>();
    private final Map<String, String> operatorRegions = new HashMap<>();
    private final Map<String, Boolean> operatorAvailability = new HashMap<>();

    @PostConstruct
    public void init() {
        registerOperator("OP-001", Arrays.asList("INSTALL", "CONFIG", "TEST"), "REGION-A");
        operatorLoads.put("OP-001", 0);
        operatorAvailability.put("OP-001", true);

        registerOperator("OP-002", Arrays.asList("SUPPORT", "NETWORK"), "REGION-B");
        operatorLoads.put("OP-002", 0);
        operatorAvailability.put("OP-002", true);

        registerOperator("OP-003", Arrays.asList("INSTALL", "TEST"), "REGION-C");
        operatorLoads.put("OP-003", 0);
        operatorAvailability.put("OP-003", true);

        registerOperator("OP-004", Arrays.asList("CONFIG", "NETWORK"), "REGION-D");
        operatorLoads.put("OP-004", 0);
        operatorAvailability.put("OP-004", true);

        registerOperator("OP-005", Arrays.asList("SUPPORT", "TEST", "INSTALL"), "REGION-A");
        operatorLoads.put("OP-005", 0);
        operatorAvailability.put("OP-005", true);
    }

    public double calculateScore(DispatchOrder order, Operator operator) {
        double skillScore = calculateSkillScore(operator.getId(), order.getSkillRequired());
        double loadScore = calculateLoadScore(operator.getId());
        double proximityScore = calculateProximityScore(operator.getId(), order.getTargetRegionCode());
        double availabilityScore = calculateAvailabilityScore(operator.getId());

        return skillScore * 0.4 + loadScore * 0.3 + proximityScore * 0.2 + availabilityScore * 0.1;
    }

    public double calculateScore(String operatorId, DispatchOrder order) {
        double skillScore = calculateSkillScore(operatorId, order.getSkillRequired());
        double loadScore = calculateLoadScore(operatorId);
        double proximityScore = calculateProximityScore(operatorId, order.getTargetRegionCode());
        double availabilityScore = calculateAvailabilityScore(operatorId);

        return skillScore * 0.4 + loadScore * 0.3 + proximityScore * 0.2 + availabilityScore * 0.1;
    }

    public double calculateSkillScore(String operatorId, String skillRequired) {
        List<String> skills = operatorSkills.get(operatorId);
        if (skills == null || skills.isEmpty()) {
            return 0.0;
        }
        if (skillRequired == null || skillRequired.isEmpty()) {
            return 0.5;
        }
        if (skills.contains(skillRequired)) {
            return 1.0;
        }
        return 0.5;
    }

    public double calculateLoadScore(String operatorId) {
        int load = operatorLoads.getOrDefault(operatorId, 0);
        return 1.0 - (load / 10.0);
    }

    public double calculateProximityScore(String operatorId, String regionCode) {
        String operatorRegion = operatorRegions.get(operatorId);
        if (operatorRegion == null) {
            return 0.2;
        }
        if (operatorRegion.equals(regionCode)) {
            return 1.0;
        }
        if (isAdjacentRegion(operatorRegion, regionCode)) {
            return 0.5;
        }
        return 0.2;
    }

    public double calculateAvailabilityScore(String operatorId) {
        Boolean available = operatorAvailability.get(operatorId);
        return Boolean.TRUE.equals(available) ? 1.0 : 0.0;
    }

    public void registerOperator(String operatorId, List<String> skills, String regionCode) {
        operatorSkills.put(operatorId, new ArrayList<>(skills));
        operatorRegions.put(operatorId, regionCode);
        operatorAvailability.putIfAbsent(operatorId, true);
        operatorLoads.putIfAbsent(operatorId, 0);
    }

    public void updateOperatorLoad(String operatorId, int load) {
        operatorLoads.put(operatorId, load);
    }

    public void setOperatorAvailable(String operatorId, boolean available) {
        operatorAvailability.put(operatorId, available);
    }

    public List<String> getOperatorSkills(String operatorId) {
        return operatorSkills.getOrDefault(operatorId, Collections.emptyList());
    }

    public String getOperatorRegion(String operatorId) {
        return operatorRegions.get(operatorId);
    }

    public List<String> getAllOperatorIds() {
        return new ArrayList<>(operatorSkills.keySet());
    }

    public Map<String, Double> getScoreBreakdown(String operatorId, DispatchOrder order) {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        double skillScore = calculateSkillScore(operatorId, order.getSkillRequired());
        double loadScore = calculateLoadScore(operatorId);
        double proximityScore = calculateProximityScore(operatorId, order.getTargetRegionCode());
        double availabilityScore = calculateAvailabilityScore(operatorId);
        double total = skillScore * 0.4 + loadScore * 0.3 + proximityScore * 0.2 + availabilityScore * 0.1;

        breakdown.put("skill", skillScore);
        breakdown.put("load", loadScore);
        breakdown.put("proximity", proximityScore);
        breakdown.put("availability", availabilityScore);
        breakdown.put("total", total);
        return breakdown;
    }

    public Map<String, Double> calculateScoresForAllOperators(DispatchOrder order) {
        Map<String, Double> allScores = new HashMap<>();
        for (String operatorId : operatorSkills.keySet()) {
            allScores.put(operatorId, calculateScore(operatorId, order));
        }
        return allScores;
    }

    public void resetAllScores() {
        operatorLoads.replaceAll((k, v) -> 0);
        operatorAvailability.replaceAll((k, v) -> true);
    }

    private boolean isAdjacentRegion(String regionA, String regionB) {
        Map<String, List<String>> adjacency = new HashMap<>();
        adjacency.put("REGION-A", Arrays.asList("REGION-B", "REGION-C"));
        adjacency.put("REGION-B", Arrays.asList("REGION-A", "REGION-D"));
        adjacency.put("REGION-C", Arrays.asList("REGION-A", "REGION-D"));
        adjacency.put("REGION-D", Arrays.asList("REGION-B", "REGION-C"));

        List<String> adjacent = adjacency.get(regionA);
        return adjacent != null && adjacent.contains(regionB);
    }
}
