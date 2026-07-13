package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.workorder.domain.WorkOrderHistory;
import com.example.telecom.workorder.repository.WorkOrderHistoryRepository;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderHistoryServiceTest {

    private WorkOrderHistoryService historyService;
    private WorkOrderHistoryRepository historyRepository;
    private WorkOrderRepository workOrderRepository;
    private String workOrderId;

    @BeforeEach
    void setUp() {
        historyRepository = new WorkOrderHistoryRepository();
        workOrderRepository = new WorkOrderRepository();
        historyService = new WorkOrderHistoryService(historyRepository, workOrderRepository);

        // Add a work order via fixture factory
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo.setWorkOrderId("wo-test-history");
        workOrderRepository.save(wo);
        workOrderId = wo.getWorkOrderId();
    }

    @Test
    void shouldRecordChange() {
        WorkOrderHistory history = historyService.recordChange(
                workOrderId, "status", "CREATED", "ASSIGNED", "operator-1", "Assigning to team");

        assertNotNull(history);
        assertNotNull(history.getHistoryId());
        assertEquals(workOrderId, history.getWorkOrderId());
        assertEquals("status", history.getFieldName());
        assertEquals("CREATED", history.getOldValue());
        assertEquals("ASSIGNED", history.getNewValue());
        assertEquals("operator-1", history.getOperatorId());
    }

    @Test
    void shouldGetHistoryByWorkOrderId() {
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "operator-1", "Assigning");
        historyService.recordChange(workOrderId, "assignee", null, "operator-1", "operator-1", "Self-assign");

        List<WorkOrderHistory> histories = historyService.getHistoryByWorkOrderId(workOrderId);
        assertNotNull(histories);
        assertEquals(2, histories.size());
    }

    @Test
    void shouldDeleteHistory() {
        WorkOrderHistory history = historyService.recordChange(
                workOrderId, "status", "CREATED", "ASSIGNED", "operator-1", "Assigning");

        assertNotNull(history.getHistoryId());

        historyService.deleteHistory(history.getHistoryId());

        List<WorkOrderHistory> histories = historyService.getHistoryByWorkOrderId(workOrderId);
        assertTrue(histories.isEmpty());
    }

    @Test
    void shouldGetLatestHistory() {
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "op-1", "Assign");
        historyService.recordChange(workOrderId, "status", "ASSIGNED", "PROCESSING", "op-1", "Start");

        WorkOrderHistory latest = historyService.getLatestHistory(workOrderId).orElse(null);
        assertNotNull(latest);
        assertTrue("PROCESSING".equals(latest.getNewValue()) || "ASSIGNED".equals(latest.getNewValue()));
    }

    @Test
    void shouldGetStatusTransitionHistory() {
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "op-1", null);
        historyService.recordChange(workOrderId, "assignee", null, "op-1", "op-1", null);
        historyService.recordChange(workOrderId, "status", "ASSIGNED", "PROCESSING", "op-1", null);

        List<WorkOrderHistory> transitions = historyService.getStatusTransitionHistory(workOrderId);
        assertEquals(2, transitions.size());
        assertTrue(transitions.stream().allMatch(h -> "status".equals(h.getFieldName())));
    }

    @Test
    void shouldGetChangeCountByField() {
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "op-1", null);
        historyService.recordChange(workOrderId, "status", "ASSIGNED", "PROCESSING", "op-1", null);
        historyService.recordChange(workOrderId, "assignee", null, "op-1", "op-1", null);

        Map<String, Long> counts = historyService.getChangeCountByField(workOrderId);
        assertEquals(2L, counts.get("status").longValue());
        assertEquals(1L, counts.get("assignee").longValue());
    }

    @Test
    void shouldGetHistoryByOperatorId() {
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "op-1", null);
        historyService.recordChange(workOrderId, "status", "ASSIGNED", "PROCESSING", "op-2", null);

        List<WorkOrderHistory> op1History = historyService.getHistoryByOperatorId("op-1");
        assertEquals(1, op1History.size());
    }

    @Test
    void shouldGetWorkOrderTimeline() {
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "op-1", "Initial assign");
        historyService.recordChange(workOrderId, "status", "ASSIGNED", "PROCESSING", "op-1", "Start work");

        Map<String, Object> timeline = historyService.getWorkOrderTimeline(workOrderId);
        assertNotNull(timeline);
        assertEquals(workOrderId, timeline.get("workOrderId"));
        assertEquals(2, timeline.get("totalChanges"));
    }

    @Test
    void shouldGetHistorySinceTimestamp() {
        long before = System.currentTimeMillis();
        historyService.recordChange(workOrderId, "status", "CREATED", "ASSIGNED", "op-1", null);

        List<WorkOrderHistory> recent = historyService.getHistorySince(before);
        assertFalse(recent.isEmpty());
    }
}
