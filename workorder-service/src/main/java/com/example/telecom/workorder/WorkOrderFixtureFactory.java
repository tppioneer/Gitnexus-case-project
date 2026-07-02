package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;

/**
 * Test fixture factory for creating WorkOrder instances with realistic data.
 * Used by test classes to create consistent test data.
 */
public class WorkOrderFixtureFactory {

    public static WorkOrder createdWorkOrder(String regionCode) {
        return new WorkOrder(
                "wo-" + System.nanoTime(),
                "alarm-001",
                "dev-001",
                WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH,
                "Network fault detected",
                "Critical alarm triggered for device in region " + regionCode,
                regionCode,
                System.currentTimeMillis()
        );
    }

    public static WorkOrder assignedWorkOrder(String regionCode, String assignee) {
        WorkOrder wo = createdWorkOrder(regionCode);
        wo.setStatus(WorkOrderStatus.ASSIGNED);
        wo.setAssignee(assignee);
        return wo;
    }

    public static WorkOrder processingWorkOrder(String regionCode, String assignee) {
        WorkOrder wo = assignedWorkOrder(regionCode, assignee);
        wo.setStatus(WorkOrderStatus.PROCESSING);
        return wo;
    }

    public static WorkOrder resolvedWorkOrder(String regionCode, String assignee) {
        WorkOrder wo = processingWorkOrder(regionCode, assignee);
        wo.setStatus(WorkOrderStatus.RESOLVED);
        return wo;
    }

    public static WorkOrder escalatedWorkOrder(String regionCode, String assignee) {
        WorkOrder wo = processingWorkOrder(regionCode, assignee);
        wo.setStatus(WorkOrderStatus.ESCALATED);
        wo.setPriority(WorkOrderPriority.CRITICAL);
        return wo;
    }

    public static WorkOrder closedWorkOrder(String regionCode, String assignee) {
        WorkOrder wo = resolvedWorkOrder(regionCode, assignee);
        wo.setStatus(WorkOrderStatus.CLOSED);
        return wo;
    }

    public static WorkOrder cancelledWorkOrder(String regionCode) {
        WorkOrder wo = assignedWorkOrder(regionCode, "op-001");
        wo.setStatus(WorkOrderStatus.CANCELLED);
        return wo;
    }
}
