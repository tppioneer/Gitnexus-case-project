package com.example.telecom.dispatch.rule;

import com.example.telecom.common.dispatch.DispatchPriority;
import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;

public class PriorityOverrideRule implements DispatchRule {

    private int priority = 15;
    private String name = "PriorityOverrideRule";

    @Override
    public DispatchRuleResult evaluate(DispatchContext context) {
        DispatchPriority dispatchPriority = context.getPriority();

        if (dispatchPriority == DispatchPriority.CRITICAL) {
            return new DispatchRuleResult(name, 1.5, true, "Critical priority override bonus applied");
        } else if (dispatchPriority == DispatchPriority.HIGH) {
            return new DispatchRuleResult(name, 1.2, true, "High priority override bonus applied");
        } else if (dispatchPriority == DispatchPriority.MEDIUM) {
            return new DispatchRuleResult(name, 1.0, true, "Medium priority override, standard score");
        } else if (dispatchPriority == DispatchPriority.LOW) {
            return new DispatchRuleResult(name, 0.8, true, "Low priority override, reduced score");
        }

        return new DispatchRuleResult(name, 1.0, true, "Unknown priority, default score");
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
