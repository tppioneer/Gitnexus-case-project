package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.event.WorkOrderEventPublisher;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;

/**
 * Manages work order state transitions.
 * Calls WorkOrderAssignmentService.assign and WorkOrderStateMachine.transition.
 */
public class WorkOrderFlowService {

    private final WorkOrderAssignmentService workOrderAssignmentService;
    private final WorkOrderStateMachine workOrderStateMachine;
    private final WorkOrderEventPublisher workOrderEventPublisher;

    public WorkOrderFlowService(WorkOrderAssignmentService workOrderAssignmentService,
                                 WorkOrderStateMachine workOrderStateMachine,
                                 WorkOrderEventPublisher workOrderEventPublisher) {
        this.workOrderAssignmentService = workOrderAssignmentService;
        this.workOrderStateMachine = workOrderStateMachine;
        this.workOrderEventPublisher = workOrderEventPublisher;
    }

    public WorkOrder assign(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderAssignmentService.assign(workOrder);
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.ASSIGNED);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    public WorkOrder startProcessing(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.PROCESSING);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    public WorkOrder resolve(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.RESOLVED);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    public WorkOrder close(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.CLOSED);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    public WorkOrder escalate(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.ESCALATED);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    public WorkOrder cancel(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.CANCELLED);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }
}
