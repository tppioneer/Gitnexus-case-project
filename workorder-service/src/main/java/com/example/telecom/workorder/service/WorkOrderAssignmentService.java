package com.example.telecom.workorder.service;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.workorder.assignment.AssigneeSelector;
import com.example.telecom.workorder.assignment.AssigneeSelectorRegistry;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.List;

/**
 * Assigns work orders to operators using the configured strategy.
 */
public class WorkOrderAssignmentService {

    private final AssigneeSelectorRegistry assigneeSelectorRegistry;
    private final WorkOrderRepository workOrderRepository;

    // In-memory list of operators (simulated)
    private final List<OperatorUser> operators;

    public WorkOrderAssignmentService(AssigneeSelectorRegistry assigneeSelectorRegistry,
                                       WorkOrderRepository workOrderRepository,
                                       List<OperatorUser> operators) {
        this.assigneeSelectorRegistry = assigneeSelectorRegistry;
        this.workOrderRepository = workOrderRepository;
        this.operators = operators;
    }

    public void assign(WorkOrder workOrder) {
        AssigneeSelector selector = assigneeSelectorRegistry.resolve("RegionBased");
        OperatorUser assignee = selector.select(workOrder, operators);
        if (assignee != null) {
            workOrder.setAssignee(assignee.getUserId());
        }
    }
}
