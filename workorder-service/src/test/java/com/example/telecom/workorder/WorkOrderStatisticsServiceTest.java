package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderStatisticsServiceTest {

    private WorkOrderStatisticsService statisticsService;
    private WorkOrderRepository workOrderRepository;

    @BeforeEach
    void setUp() {
        workOrderRepository = new WorkOrderRepository();
        statisticsService = new WorkOrderStatisticsService(workOrderRepository);

        // Add diverse test data with different statuses and regions
        workOrderRepository.save(new WorkOrder("wo-1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "Fault 1", "desc", "EAST", System.currentTimeMillis()));

        WorkOrder wo2 = WorkOrderFixtureFactory.assignedWorkOrder("WEST", "op-1");
        wo2.setWorkOrderId("wo-2");
        workOrderRepository.save(wo2);

        WorkOrder wo3 = WorkOrderFixtureFactory.processingWorkOrder("EAST", "op-2");
        wo3.setWorkOrderId("wo-3");
        workOrderRepository.save(wo3);

        WorkOrder wo4 = WorkOrderFixtureFactory.resolvedWorkOrder("SOUTH", "op-3");
        wo4.setWorkOrderId("wo-4");
        workOrderRepository.save(wo4);

        WorkOrder wo5 = WorkOrderFixtureFactory.closedWorkOrder("EAST", "op-1");
        wo5.setWorkOrderId("wo-5");
        workOrderRepository.save(wo5);

        WorkOrder wo6 = WorkOrderFixtureFactory.cancelledWorkOrder("NORTH");
        wo6.setWorkOrderId("wo-6");
        workOrderRepository.save(wo6);
    }

    @Test
    void shouldCountByStatus() {
        Map<WorkOrderStatus, Long> counts = statisticsService.countByStatus();
        assertEquals(6, counts.values().stream().mapToLong(Long::longValue).sum());
    }

    @Test
    void shouldCountByRegion() {
        Map<String, Long> byRegion = statisticsService.countByRegion();
        assertTrue(byRegion.containsKey("EAST"));
        assertTrue(byRegion.containsKey("WEST"));
    }

    @Test
    void shouldCountOpenWorkOrders() {
        long open = statisticsService.countOpenWorkOrders();
        assertTrue(open >= 3); // at least CREATED + ASSIGNED + PROCESSING
    }

    @Test
    void shouldGenerateReport() {
        String report = statisticsService.generateReport();
        assertTrue(report.contains("total="));
        assertTrue(report.contains("open="));
    }

    @Test
    void shouldFindStaleWorkOrders() {
        // With a very high threshold, no work orders should be stale
        var stale = statisticsService.findStaleWorkOrders(999999);
        assertTrue(stale.isEmpty(), "No WOs should be stale with very high threshold");
    }
}
