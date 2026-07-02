package com.example.telecom.workorder;

import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderStateMachineTest {

    private WorkOrderStateMachine stateMachine;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        stateMachine = new WorkOrderStateMachine();
        workOrder = new WorkOrder("wo-1", "alarm-1", "dev-1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "Test WO", "desc", "EAST", System.currentTimeMillis());
    }

    @Test
    void shouldTransitionCreatedToAssigned() {
        stateMachine.transition(workOrder, WorkOrderStatus.ASSIGNED);
        assertEquals(WorkOrderStatus.ASSIGNED, workOrder.getStatus());
    }

    @Test
    void shouldTransitionAssignedToProcessing() {
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        stateMachine.transition(workOrder, WorkOrderStatus.PROCESSING);
        assertEquals(WorkOrderStatus.PROCESSING, workOrder.getStatus());
    }

    @Test
    void shouldTransitionProcessingToResolved() {
        workOrder.setStatus(WorkOrderStatus.PROCESSING);
        stateMachine.transition(workOrder, WorkOrderStatus.RESOLVED);
        assertEquals(WorkOrderStatus.RESOLVED, workOrder.getStatus());
    }

    @Test
    void shouldTransitionResolvedToClosed() {
        workOrder.setStatus(WorkOrderStatus.RESOLVED);
        stateMachine.transition(workOrder, WorkOrderStatus.CLOSED);
        assertEquals(WorkOrderStatus.CLOSED, workOrder.getStatus());
    }

    @Test
    void shouldTransitionProcessingToEscalated() {
        workOrder.setStatus(WorkOrderStatus.PROCESSING);
        stateMachine.transition(workOrder, WorkOrderStatus.ESCALATED);
        assertEquals(WorkOrderStatus.ESCALATED, workOrder.getStatus());
    }

    @Test
    void shouldTransitionEscalatedToProcessing() {
        workOrder.setStatus(WorkOrderStatus.ESCALATED);
        stateMachine.transition(workOrder, WorkOrderStatus.PROCESSING);
        assertEquals(WorkOrderStatus.PROCESSING, workOrder.getStatus());
    }

    @Test
    void shouldTransitionAssignedToCancelled() {
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        stateMachine.transition(workOrder, WorkOrderStatus.CANCELLED);
        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getStatus());
    }

    @Test
    void shouldRejectInvalidTransition() {
        assertThrows(ValidationException.class,
                () -> stateMachine.transition(workOrder, WorkOrderStatus.RESOLVED));
    }

    @Test
    void shouldRejectTransitionFromClosed() {
        workOrder.setStatus(WorkOrderStatus.CLOSED);
        assertThrows(ValidationException.class,
                () -> stateMachine.transition(workOrder, WorkOrderStatus.PROCESSING));
    }
}
