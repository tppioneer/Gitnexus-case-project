package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.dto.WorkOrderReportRequest;
import com.example.telecom.workorder.dto.WorkOrderReportResponse;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderReportServiceTest {

    private WorkOrderReportService reportService;
    private WorkOrderRepository workOrderRepository;

    @BeforeEach
    void setUp() {
        workOrderRepository = new WorkOrderRepository();
        reportService = new WorkOrderReportService(workOrderRepository);

        // Add diverse work orders using WorkOrderFixtureFactory
        WorkOrder wo1 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo1.setWorkOrderId("wo-report-1");
        workOrderRepository.save(wo1);

        WorkOrder wo2 = WorkOrderFixtureFactory.assignedWorkOrder("WEST", "op-1");
        wo2.setWorkOrderId("wo-report-2");
        workOrderRepository.save(wo2);

        WorkOrder wo3 = WorkOrderFixtureFactory.processingWorkOrder("EAST", "op-2");
        wo3.setWorkOrderId("wo-report-3");
        workOrderRepository.save(wo3);

        WorkOrder wo4 = WorkOrderFixtureFactory.resolvedWorkOrder("SOUTH", "op-3");
        wo4.setWorkOrderId("wo-report-4");
        workOrderRepository.save(wo4);

        WorkOrder wo5 = WorkOrderFixtureFactory.closedWorkOrder("NORTH", "op-1");
        wo5.setWorkOrderId("wo-report-5");
        workOrderRepository.save(wo5);

        WorkOrder wo6 = WorkOrderFixtureFactory.cancelledWorkOrder("EAST");
        wo6.setWorkOrderId("wo-report-6");
        workOrderRepository.save(wo6);
    }

    @Test
    void shouldGenerateReport() {
        WorkOrderReportRequest request = new WorkOrderReportRequest();
        request.setStartTime(System.currentTimeMillis() - 60_000);
        request.setEndTime(System.currentTimeMillis() + 60_000);
        request.setGroupBy("status");
        request.setFormat("json");

        WorkOrderReportResponse response = reportService.generateReport(request);
        assertNotNull(response);
        assertNotNull(response.getReportId());
        assertEquals(6, response.getTotalWorkOrders());
        assertEquals("status", response.getGroupBy());
    }

    @Test
    void shouldGenerateSummaryReport() {
        WorkOrderReportResponse response = reportService.generateSummaryReport();
        assertNotNull(response);
        assertNotNull(response.getReportId());
        assertTrue(response.getTotalWorkOrders() >= 0);
        assertNotNull(response.getSummaryData());
    }

    @Test
    void shouldExportCsv() {
        WorkOrderReportRequest request = new WorkOrderReportRequest();
        request.setStartTime(System.currentTimeMillis() - 60_000);
        request.setEndTime(System.currentTimeMillis() + 60_000);
        request.setGroupBy("status");

        String csv = reportService.exportCsv(request);
        assertNotNull(csv);
        assertTrue(csv.contains("Group,Count"));
        assertTrue(csv.contains(","));
    }

    @Test
    void shouldGetReportById() {
        WorkOrderReportRequest request = new WorkOrderReportRequest();
        request.setStartTime(System.currentTimeMillis() - 60_000);
        request.setEndTime(System.currentTimeMillis() + 60_000);
        request.setGroupBy("status");
        request.setFormat("json");

        WorkOrderReportResponse generated = reportService.generateReport(request);
        assertNotNull(generated.getReportId());

        WorkOrderReportResponse retrieved = reportService.getReportById(generated.getReportId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals(generated.getReportId(), retrieved.getReportId());
        assertEquals(generated.getTotalWorkOrders(), retrieved.getTotalWorkOrders());
    }

    @Test
    void shouldCalculateSummaryByRegion() {
        Map<String, Long> summary = reportService.calculateSummaryData(
                System.currentTimeMillis() - 60_000,
                System.currentTimeMillis() + 60_000,
                "region");
        assertNotNull(summary);
        assertTrue(summary.containsKey("EAST"));
        assertTrue(summary.containsKey("WEST"));
    }

    @Test
    void shouldCalculateSummaryByPriority() {
        Map<String, Long> summary = reportService.calculateSummaryData(
                System.currentTimeMillis() - 60_000,
                System.currentTimeMillis() + 60_000,
                "priority");
        assertNotNull(summary);
        assertTrue(summary.containsKey("HIGH"));
    }

    @Test
    void shouldListAllReports() {
        WorkOrderReportRequest request = new WorkOrderReportRequest();
        request.setStartTime(System.currentTimeMillis() - 60_000);
        request.setEndTime(System.currentTimeMillis() + 60_000);
        request.setGroupBy("status");
        reportService.generateReport(request);

        List<WorkOrderReportResponse> reports = reportService.listAllReports();
        assertFalse(reports.isEmpty());
    }

    @Test
    void shouldGetSlaSummary() {
        Map<String, Long> slaSummary = reportService.getSlaSummary(
                System.currentTimeMillis() - 60_000,
                System.currentTimeMillis() + 60_000);
        assertNotNull(slaSummary);
        assertTrue(slaSummary.containsKey("total"));
        assertTrue(slaSummary.containsKey("compliant"));
    }

    @Test
    void shouldGetOperatorPerformanceSummary() {
        Map<String, Long> perf = reportService.getOperatorPerformanceSummary(
                System.currentTimeMillis() - 60_000,
                System.currentTimeMillis() + 60_000);
        assertNotNull(perf);
    }
}
