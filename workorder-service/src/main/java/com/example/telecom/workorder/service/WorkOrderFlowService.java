package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.event.WorkOrderEventPublisher;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Transition a work order to WAITING_VENDOR state and publish event.
     */
    public WorkOrder waitForVendor(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.WAITING_VENDOR);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    /**
     * Transition a work order from WAITING_VENDOR back to PROCESSING and publish event.
     */
    public WorkOrder returnFromVendor(WorkOrder workOrder) {
        WorkOrderStatus fromStatus = workOrder.getStatus();
        workOrderStateMachine.transition(workOrder, WorkOrderStatus.PROCESSING);
        workOrderEventPublisher.publish(workOrder, fromStatus, workOrder.getStatus());
        return workOrder;
    }

    /**
     * Validate whether a transition from the work order's current status to the target is allowed.
     */
    public boolean validateTransition(WorkOrder workOrder, WorkOrderStatus target) {
        return workOrderStateMachine.canTransition(workOrder.getStatus(), target);
    }

    /**
     * Bulk assign work orders (transition to ASSIGNED for each).
     */
    public List<WorkOrder> bulkAssign(List<WorkOrder> workOrders) {
        List<WorkOrder> result = new ArrayList<>();
        for (WorkOrder wo : workOrders) {
            if (workOrderStateMachine.canTransition(wo.getStatus(), WorkOrderStatus.ASSIGNED)) {
                WorkOrderStatus fromStatus = wo.getStatus();
                workOrderStateMachine.transition(wo, WorkOrderStatus.ASSIGNED);
                workOrderEventPublisher.publish(wo, fromStatus, wo.getStatus());
                result.add(wo);
            }
        }
        return result;
    }

    /**
     * Get the full allowed path from the work order's current state as a list of "from -> to" strings.
     */
    public List<String> getWorkOrderTimeline(WorkOrder workOrder) {
        List<String> timeline = new ArrayList<>();
        WorkOrderStatus current = workOrder.getStatus();
        for (WorkOrderStatus target : WorkOrderStatus.values()) {
            if (workOrderStateMachine.canTransition(current, target)) {
                timeline.add(current.name() + " -> " + target.name());
            }
        }
        return timeline;
    }
}
