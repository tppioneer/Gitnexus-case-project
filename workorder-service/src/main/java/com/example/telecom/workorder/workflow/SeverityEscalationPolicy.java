package com.example.telecom.workorder.workflow;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;

/**
 * Escalates work orders based on priority level.
 * evaluate() method is a Case B NOISE item.
 */
public class SeverityEscalationPolicy implements EscalationPolicy {

    private static final long ESCALATION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes

    @Override
    public boolean shouldEscalate(WorkOrder workOrder) {
        if (workOrder.getPriority() == WorkOrderPriority.CRITICAL
                || workOrder.getPriority() == WorkOrderPriority.HIGH) {
            long elapsed = System.currentTimeMillis() - workOrder.getCreatedTime();
            return elapsed > ESCALATION_TIMEOUT_MS;
        }
        return false;
    }

    @Override
    public String evaluate(WorkOrder workOrder) {
        return shouldEscalate(workOrder)
                ? "ESCALATE: Work order " + workOrder.getWorkOrderId() + " requires escalation"
                : "OK: Work order " + workOrder.getWorkOrderId() + " within SLA";
    }
}
