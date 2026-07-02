package com.example.telecom.workorder;

import com.example.telecom.common.api.OperationResult;
import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.workorder.assignment.*;
import com.example.telecom.workorder.event.WorkOrderEventPublisher;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.*;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderBulkActionServiceTest {

    private WorkOrderBulkActionService bulkActionService;
    private WorkOrderRepository workOrderRepository;

    @BeforeEach
    void setUp() {
        workOrderRepository = new WorkOrderRepository();
        WorkOrderStateMachine stateMachine = new WorkOrderStateMachine();

        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent e) {}
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent e) {}
            @Override public void publish(WorkOrderEvent e) {}
        };
        WorkOrderEventPublisher publisher = new WorkOrderEventPublisher(bus);
        List<OperatorUser> operators = List.of(
                new OperatorUser("op-1", "Alice", "EAST", "111", "a@t.com", "expert"));
        AssigneeSelectorRegistry registry = new AssigneeSelectorRegistry(List.of(
                new LoadBalancedAssigneeSelector()));
        WorkOrderAssignmentService assignmentService = new WorkOrderAssignmentService(
                registry, workOrderRepository, operators);
        WorkOrderFlowService flowService = new WorkOrderFlowService(
                assignmentService, stateMachine, publisher);

        bulkActionService = new WorkOrderBulkActionService(workOrderRepository, flowService);
    }

    @Test
    void shouldBulkAssignWorkOrders() {
        WorkOrder wo1 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo1.setWorkOrderId("wo-bulk-1");
        WorkOrder wo2 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo2.setWorkOrderId("wo-bulk-2");
        workOrderRepository.save(wo1);
        workOrderRepository.save(wo2);

        OperationResult result = bulkActionService.bulkAssign(List.of("wo-bulk-1", "wo-bulk-2"));
        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccessCount());
    }

    @Test
    void shouldReportFailureForMissingWorkOrders() {
        OperationResult result = bulkActionService.bulkAssign(List.of("nonexistent-1", "nonexistent-2"));
        assertFalse(result.isSuccess());
        assertEquals(2, result.getFailureCount());
    }
}
