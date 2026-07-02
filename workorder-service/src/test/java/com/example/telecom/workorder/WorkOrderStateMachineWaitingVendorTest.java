package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderStateMachineWaitingVendorTest {

    private WorkOrderStateMachine stateMachine;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        stateMachine = new WorkOrderStateMachine();
        workOrder = new WorkOrder("wo-vendor-1", "a1", "d1", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.HIGH, "Hardware replacement", "vendor hardware issue",
                "EAST", System.currentTimeMillis());
    }

    @Test
    void shouldTransitionProcessingToWaitingVendor() {
        stateMachine.transition(workOrder, WorkOrderStatus.WAITING_VENDOR);
        assertEquals(WorkOrderStatus.WAITING_VENDOR, workOrder.getStatus());
    }

    @Test
    void shouldTransitionWaitingVendorToProcessing() {
        workOrder.setStatus(WorkOrderStatus.WAITING_VENDOR);
        stateMachine.transition(workOrder, WorkOrderStatus.PROCESSING);
        assertEquals(WorkOrderStatus.PROCESSING, workOrder.getStatus());
    }

    @Test
    void shouldTransitionWaitingVendorToResolved() {
        workOrder.setStatus(WorkOrderStatus.WAITING_VENDOR);
        stateMachine.transition(workOrder, WorkOrderStatus.RESOLVED);
        assertEquals(WorkOrderStatus.RESOLVED, workOrder.getStatus());
    }

    @Test
    void shouldTransitionWaitingVendorToEscalated() {
        workOrder.setStatus(WorkOrderStatus.WAITING_VENDOR);
        stateMachine.transition(workOrder, WorkOrderStatus.ESCALATED);
        assertEquals(WorkOrderStatus.ESCALATED, workOrder.getStatus());
    }

    @Test
    void shouldAllowWaitingVendorState() {
        assertTrue(stateMachine.canTransition(WorkOrderStatus.PROCESSING, WorkOrderStatus.WAITING_VENDOR));
        assertTrue(stateMachine.canTransition(WorkOrderStatus.WAITING_VENDOR, WorkOrderStatus.PROCESSING));
    }
}
