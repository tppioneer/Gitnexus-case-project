package com.example.telecom.workorder.workflow;

import com.example.telecom.common.workorder.WorkOrder;

public interface EscalationPolicy {
    boolean shouldEscalate(WorkOrder workOrder);

    /**
     * Evaluate whether escalation is needed for a work order.
     * This is a Case B NOISE item — same method name 'evaluate' as RuleEvaluator,
     * but semantically unrelated.
     */
    String evaluate(WorkOrder workOrder);
}
