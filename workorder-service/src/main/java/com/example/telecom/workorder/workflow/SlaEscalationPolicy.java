package com.example.telecom.workorder.workflow;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;

/**
 * Escalates work orders based on SLA time thresholds.
 * evaluate() method is a Case B NOISE item.
 */
public class SlaEscalationPolicy implements EscalationPolicy {

    @Override
    public boolean shouldEscalate(WorkOrder workOrder) {
        long slaMs = getSlaMillis(workOrder.getPriority());
        long elapsed = System.currentTimeMillis() - workOrder.getCreatedTime();
        return elapsed > slaMs;
    }

    @Override
    public String evaluate(WorkOrder workOrder) {
        long slaMs = getSlaMillis(workOrder.getPriority());
        long elapsed = System.currentTimeMillis() - workOrder.getCreatedTime();
        long remaining = slaMs - elapsed;
        if (remaining <= 0) {
            return "SLA BREACHED: Work order " + workOrder.getWorkOrderId();
        }
        return "SLA OK: " + (remaining / 60000) + " minutes remaining for " + workOrder.getWorkOrderId();
    }

    private long getSlaMillis(WorkOrderPriority priority) {
        return switch (priority) {
            case CRITICAL -> 15 * 60 * 1000L;    // 15 min
            case HIGH -> 60 * 60 * 1000L;         // 1 hour
            case MEDIUM -> 4 * 60 * 60 * 1000L;   // 4 hours
            case LOW -> 24 * 60 * 60 * 1000L;     // 24 hours
        };
    }
}
