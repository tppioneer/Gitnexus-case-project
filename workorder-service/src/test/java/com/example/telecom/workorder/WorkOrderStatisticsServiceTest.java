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
        assertTrue(byRegion.containsKey("SOUTH"));
        assertTrue(byRegion.containsKey("NORTH"));
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

    @Test
    void shouldReturnOverview() {
        Map<String, Object> overview = statisticsService.getOverview();
        assertNotNull(overview);
        assertTrue(overview.containsKey("total"));
        assertTrue(overview.containsKey("open"));
        assertTrue(overview.containsKey("closed"));
        assertTrue(overview.containsKey("cancelled"));
        assertTrue(overview.containsKey("breached"));
        assertTrue(overview.containsKey("avgResolutionMinutes"));
        assertEquals(6L, overview.get("total"));
    }

    @Test
    void shouldReturnStatusDistribution() {
        Map<String, Long> distribution = statisticsService.getStatusDistribution();
        assertNotNull(distribution);
        assertEquals(6, distribution.values().stream().mapToLong(Long::longValue).sum());
        assertTrue(distribution.containsKey("CREATED"));
        assertTrue(distribution.containsKey("ASSIGNED"));
        assertTrue(distribution.containsKey("PROCESSING"));
        assertTrue(distribution.containsKey("RESOLVED"));
        assertTrue(distribution.containsKey("CLOSED"));
        assertTrue(distribution.containsKey("CANCELLED"));
    }

    @Test
    void shouldReturnPriorityDistribution() {
        Map<WorkOrderPriority, Long> distribution = statisticsService.getPriorityDistribution();
        assertNotNull(distribution);
        // All work orders are HIGH priority from the fixture factory
        assertEquals(6, distribution.values().stream().mapToLong(Long::longValue).sum());
        assertTrue(distribution.containsKey(WorkOrderPriority.HIGH));
    }

    @Test
    void shouldReturnTrend() {
        long from = System.currentTimeMillis() - 60_000;
        long to = System.currentTimeMillis() + 60_000;
        Map<String, Long> trend = statisticsService.getTrend(from, to);
        assertNotNull(trend);
        // All work orders were just created, so they should all be within range
        long totalInRange = trend.values().stream().mapToLong(Long::longValue).sum();
        assertEquals(6, totalInRange);
    }

    @Test
    void shouldReturnOperatorStats() {
        Map<String, Object> stats = statisticsService.getOperatorStats("op-1");
        assertNotNull(stats);
        assertTrue(stats.containsKey("assignedCount"));
        assertTrue(stats.containsKey("resolvedCount"));
        assertTrue(stats.containsKey("openCount"));
        assertTrue(stats.containsKey("avgResolutionMinutes"));
    }

    @Test
    void shouldReturnCategoryDistribution() {
        Map<String, Long> distribution = statisticsService.getCategoryDistribution();
        assertNotNull(distribution);
        assertTrue(distribution.containsKey("EAST"));
        assertTrue(distribution.containsKey("WEST"));
        assertTrue(distribution.containsKey("SOUTH"));
        assertTrue(distribution.containsKey("NORTH"));
    }

    @Test
    void shouldEvaluateWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        String evaluation = statisticsService.evaluate(wo);
        assertNotNull(evaluation);
        assertTrue(evaluation.equals("ON_TRACK") || evaluation.equals("AT_RISK") || evaluation.equals("BREACHED"));
    }

    @Test
    void shouldReturnAverageResolutionTime() {
        double avg = statisticsService.averageResolutionTimeMinutes();
        assertTrue(avg >= 0);
    }

    @Test
    void shouldCountSlaBreached() {
        long breached = statisticsService.countSlaBreached();
        assertTrue(breached >= 0);
    }

    @Test
    void shouldCountByAssignee() {
        Map<String, Long> byAssignee = statisticsService.countByAssignee();
        assertNotNull(byAssignee);
        assertTrue(byAssignee.containsKey("op-1") || byAssignee.containsKey("op-2") || byAssignee.containsKey("op-3"));
    }
}
