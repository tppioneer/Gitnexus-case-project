package com.example.telecom.dispatch.rule;

import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;

public interface DispatchRule {

    DispatchRuleResult evaluate(DispatchContext context);

    int getPriority();
}
