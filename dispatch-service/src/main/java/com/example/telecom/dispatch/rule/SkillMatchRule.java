package com.example.telecom.dispatch.rule;

import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;

import java.util.*;
import java.util.stream.Collectors;

public class SkillMatchRule implements DispatchRule {

    private final int priority;
    private final String name;
    private final Map<String, List<String>> operatorSkills;

    public SkillMatchRule() {
        this.priority = 10;
        this.name = "SkillMatchRule";
        this.operatorSkills = new HashMap<>();
    }

    public void registerOperatorSkills(String operatorId, List<String> skills) {
        operatorSkills.put(operatorId, new ArrayList<>(skills));
    }

    @Override
    public DispatchRuleResult evaluate(DispatchContext context) {
        List<String> requiredSkills = context.getRequiredSkills();
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new DispatchRuleResult(name, 1.0, true, "No skills required");
        }

        List<String> availableOperators = context.getAvailableOperators();
        if (availableOperators == null || availableOperators.isEmpty()) {
            return new DispatchRuleResult(name, 0.0, false, "No available operators");
        }

        double totalMatchRatio = 0.0;
        int operatorCount = 0;

        for (String operatorId : availableOperators) {
            List<String> skills = operatorSkills.get(operatorId);
            if (skills != null && !skills.isEmpty()) {
                double matchRatio = matchSkills(requiredSkills, skills);
                totalMatchRatio += matchRatio;
                operatorCount++;
            }
        }

        if (operatorCount == 0) {
            return new DispatchRuleResult(name, 0.0, false, "No operators have registered skills");
        }

        double averageMatchRatio = totalMatchRatio / operatorCount;
        boolean passed = averageMatchRatio > 0.5;

        return new DispatchRuleResult(name, averageMatchRatio, passed,
                String.format("Skill match ratio: %.2f across %d operators", averageMatchRatio, operatorCount));
    }

    private double matchSkills(List<String> required, List<String> available) {
        if (required.isEmpty()) {
            return 1.0;
        }
        long matchCount = required.stream()
                .filter(available::contains)
                .count();
        return (double) matchCount / required.size();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }
}
