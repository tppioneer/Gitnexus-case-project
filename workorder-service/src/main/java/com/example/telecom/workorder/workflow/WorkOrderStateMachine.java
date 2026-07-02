package com.example.telecom.workorder.workflow;

import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;

import java.util.Map;
import java.util.Set;

/**
 * Work order state machine.
 * States: CREATED → ASSIGNED → PROCESSING → RESOLVED → CLOSED
 *         PROCESSING → WAITING_VENDOR → PROCESSING / RESOLVED / ESCALATED
 *         PROCESSING → ESCALATED → PROCESSING (loop)
 *         ASSIGNED → CANCELLED
 */
public class WorkOrderStateMachine {

    private static final Map<WorkOrderStatus, Set<WorkOrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            WorkOrderStatus.CREATED, Set.of(WorkOrderStatus.ASSIGNED),
            WorkOrderStatus.ASSIGNED, Set.of(WorkOrderStatus.PROCESSING, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.PROCESSING, Set.of(WorkOrderStatus.WAITING_VENDOR, WorkOrderStatus.RESOLVED, WorkOrderStatus.ESCALATED),
            WorkOrderStatus.WAITING_VENDOR, Set.of(WorkOrderStatus.PROCESSING, WorkOrderStatus.RESOLVED, WorkOrderStatus.ESCALATED),
            WorkOrderStatus.ESCALATED, Set.of(WorkOrderStatus.PROCESSING),
            WorkOrderStatus.RESOLVED, Set.of(WorkOrderStatus.CLOSED),
            WorkOrderStatus.CLOSED, Set.of(),
            WorkOrderStatus.CANCELLED, Set.of()
    );

    public void transition(WorkOrder workOrder, WorkOrderStatus targetStatus) {
        WorkOrderStatus currentStatus = workOrder.getStatus();
        Set<WorkOrderStatus> allowed = ALLOWED_TRANSITIONS.get(currentStatus);

        if (allowed == null || !allowed.contains(targetStatus)) {
            throw new ValidationException("status",
                    "Invalid transition: " + currentStatus + " → " + targetStatus);
        }

        workOrder.setStatus(targetStatus);
        workOrder.setUpdatedTime(System.currentTimeMillis());
    }

    public boolean canTransition(WorkOrderStatus from, WorkOrderStatus to) {
        Set<WorkOrderStatus> allowed = ALLOWED_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public Set<WorkOrderStatus> getNextStates(WorkOrderStatus current) {
        return ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
    }
}
