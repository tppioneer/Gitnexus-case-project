package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.service.WorkOrderSlaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderSlaServiceTest {

    private WorkOrderSlaService slaService;

    @BeforeEach
    void setUp() {
        slaService = new WorkOrderSlaService();
    }

    @Test
    void shouldNotBreachSlaForNewWorkOrder() {
        WorkOrder wo = new WorkOrder("wo-1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "Test", "desc", "EAST", System.currentTimeMillis());
        assertFalse(slaService.isSlaBreached(wo));
    }

    @Test
    void shouldBreachSlaForOldCriticalWorkOrder() {
        WorkOrder wo = new WorkOrder("wo-old", "a2", "d2", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Old WO", "desc", "EAST",
                System.currentTimeMillis() - 3600000);
        assertTrue(slaService.isSlaBreached(wo));
    }

    @Test
    void shouldReturnPositiveRemainingTimeForNewWorkOrder() {
        WorkOrder wo = new WorkOrder("wo-new", "a3", "d3", WorkOrderStatus.CREATED,
                WorkOrderPriority.MEDIUM, "New", "desc", "WEST", System.currentTimeMillis());
        assertTrue(slaService.getSlaRemainingMs(wo) > 0);
    }

    @Test
    void shouldReturnZeroRemainingTimeForBreachedWorkOrder() {
        WorkOrder wo = new WorkOrder("wo-old", "a4", "d4", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Old", "desc", "SOUTH",
                System.currentTimeMillis() - 3600000);
        assertEquals(0, slaService.getSlaRemainingMs(wo));
    }

    @Test
    void shouldCalculateDifferentSlaByPriority() {
        WorkOrder criticalWo = new WorkOrder("w1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.CRITICAL, "t", "d", "E", System.currentTimeMillis());
        WorkOrder lowWo = new WorkOrder("w2", "a2", "d2", WorkOrderStatus.CREATED,
                WorkOrderPriority.LOW, "t", "d", "W", System.currentTimeMillis());

        long criticalRemaining = slaService.getSlaRemainingMs(criticalWo);
        long lowRemaining = slaService.getSlaRemainingMs(lowWo);

        // LOW priority should have much longer SLA than CRITICAL
        assertTrue(lowRemaining > criticalRemaining);
    }

    @Test
    void shouldReportSlaBreachedForExactlyZeroRemaining() {
        WorkOrder wo = new WorkOrder("wo-zero", "a5", "d5", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Zero", "desc", "EAST",
                System.currentTimeMillis() - 15 * 60 * 1000L - 1);
        assertTrue(slaService.isSlaBreached(wo));
    }
}
