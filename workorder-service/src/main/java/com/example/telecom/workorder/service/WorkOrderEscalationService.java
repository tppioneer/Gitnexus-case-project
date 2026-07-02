package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.workflow.EscalationPolicy;

import java.util.List;

/**
 * Evaluates and executes work order escalations.
 */
public class WorkOrderEscalationService {

    private final List<EscalationPolicy> escalationPolicies;
    private final WorkOrderFlowService workOrderFlowService;

    public WorkOrderEscalationService(List<EscalationPolicy> escalationPolicies,
                                       WorkOrderFlowService workOrderFlowService) {
        this.escalationPolicies = escalationPolicies;
        this.workOrderFlowService = workOrderFlowService;
    }

    public void checkAndEscalate(WorkOrder workOrder) {
        if (workOrder.getStatus() != WorkOrderStatus.PROCESSING) {
            return;
        }
        for (EscalationPolicy policy : escalationPolicies) {
            if (policy.shouldEscalate(workOrder)) {
                workOrderFlowService.escalate(workOrder);
                return; // escalate once
            }
        }
    }
}
