package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderFixtureFactoryTest {

    @Test
    void shouldCreateCreatedWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        assertEquals(WorkOrderStatus.CREATED, wo.getStatus());
        assertEquals("EAST", wo.getMaintenanceRegionCode());
        assertNotNull(wo.getWorkOrderId());
    }

    @Test
    void shouldCreateAssignedWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.assignedWorkOrder("WEST", "op-1");
        assertEquals(WorkOrderStatus.ASSIGNED, wo.getStatus());
        assertEquals("op-1", wo.getAssignee());
        assertEquals("WEST", wo.getMaintenanceRegionCode());
    }

    @Test
    void shouldCreateProcessingWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.processingWorkOrder("SOUTH", "op-2");
        assertEquals(WorkOrderStatus.PROCESSING, wo.getStatus());
        assertEquals("op-2", wo.getAssignee());
    }

    @Test
    void shouldCreateResolvedWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.resolvedWorkOrder("NORTH", "op-3");
        assertEquals(WorkOrderStatus.RESOLVED, wo.getStatus());
    }

    @Test
    void shouldCreateEscalatedWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.escalatedWorkOrder("EAST", "op-1");
        assertEquals(WorkOrderStatus.ESCALATED, wo.getStatus());
        assertEquals(com.example.telecom.common.workorder.WorkOrderPriority.CRITICAL, wo.getPriority());
    }

    @Test
    void shouldCreateClosedWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.closedWorkOrder("WEST", "op-1");
        assertEquals(WorkOrderStatus.CLOSED, wo.getStatus());
    }

    @Test
    void shouldCreateCancelledWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.cancelledWorkOrder("SOUTH");
        assertEquals(WorkOrderStatus.CANCELLED, wo.getStatus());
    }

    @Test
    void allFixturesShouldHaveUniqueIds() {
        WorkOrder wo1 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        WorkOrder wo2 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        assertNotEquals(wo1.getWorkOrderId(), wo2.getWorkOrderId());
    }
}
