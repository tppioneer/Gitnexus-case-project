package com.example.telecom.dispatch.rule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;

@Component
public class DispatchRuleRegistry {

    private final List<DispatchRule> rules = new ArrayList<>();

    public void register(DispatchRule rule) {
        rules.add(rule);
        rules.sort(Comparator.comparingInt(DispatchRule::getPriority));
    }

    public void unregister(String ruleName) {
        rules.removeIf(rule -> rule.getClass().getSimpleName().equals(ruleName));
    }

    public DispatchRule resolve(String ruleName) {
        for (DispatchRule rule : rules) {
            if (rule.getClass().getSimpleName().equals(ruleName)) {
                return rule;
            }
        }
        throw new DomainException("NOT_FOUND", "Rule not found: " + ruleName);
    }

    public List<DispatchRule> getAllRules() {
        return new ArrayList<>(rules);
    }

    public List<DispatchRuleResult> evaluateAll(DispatchContext context) {
        List<DispatchRuleResult> results = new ArrayList<>();
        for (DispatchRule rule : rules) {
            results.add(rule.evaluate(context));
        }
        return results;
    }
}
