package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.policy.WaitingVendorPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaitingVendorPolicyTest {

    private final WaitingVendorPolicy policy = new WaitingVendorPolicy();

    @Test
    void shouldWaitForVendorWhenDescriptionContainsHardware() {
        WorkOrder wo = createWorkOrder("Hardware replacement needed");
        assertTrue(policy.shouldWaitForVendor(wo));
    }

    @Test
    void shouldWaitForVendorWhenDescriptionContainsRMA() {
        WorkOrder wo = createWorkOrder("RMA process for defective unit");
        assertTrue(policy.shouldWaitForVendor(wo));
    }

    @Test
    void shouldNotWaitForCriticalWorkOrder() {
        WorkOrder wo = new WorkOrder("wo-crit", "a1", "d1", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Hardware failure", "vendor hardware rma",
                "EAST", System.currentTimeMillis());
        assertFalse(policy.shouldWaitForVendor(wo));
    }

    @Test
    void shouldNotWaitForNullDescription() {
        WorkOrder wo = createWorkOrder(null);
        assertFalse(policy.shouldWaitForVendor(wo));
    }

    @Test
    void evaluateShouldReturnProperMessage() {
        WorkOrder wo = createWorkOrder("vendor hardware issue");
        String result = policy.evaluate(wo);
        assertTrue(result.contains("WAIT_VENDOR"));
    }

    private WorkOrder createWorkOrder(String description) {
        return new WorkOrder("wo-test", "a-test", "d-test", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.MEDIUM, "Test", description, "EAST", System.currentTimeMillis());
    }
}
