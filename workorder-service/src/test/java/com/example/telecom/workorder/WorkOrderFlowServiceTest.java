package com.example.telecom.workorder;

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

class WorkOrderFlowServiceTest {

    private WorkOrderFlowService flowService;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent e) {}
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent e) {}
            @Override public void publish(WorkOrderEvent e) {}
        };
        WorkOrderEventPublisher publisher = new WorkOrderEventPublisher(bus);
        WorkOrderRepository repo = new WorkOrderRepository();
        AssigneeSelectorRegistry registry = new AssigneeSelectorRegistry(List.of(
                new RegionBasedAssigneeSelector(), new LoadBalancedAssigneeSelector()));
        List<OperatorUser> operators = List.of(
                new OperatorUser("op-1", "Alice", "EAST", "111", "a@t.com", "expert"));
        WorkOrderAssignmentService assignmentService = new WorkOrderAssignmentService(registry, repo, operators);

        flowService = new WorkOrderFlowService(assignmentService, new WorkOrderStateMachine(), publisher);
        workOrder = new WorkOrder("wo-1", "a1", "dev-1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "Test", "desc", "EAST", System.currentTimeMillis());
    }

    @Test
    void shouldAssignWorkOrder() {
        flowService.assign(workOrder);
        assertEquals(WorkOrderStatus.ASSIGNED, workOrder.getStatus());
    }

    @Test
    void shouldResolveWorkOrder() {
        workOrder.setStatus(WorkOrderStatus.PROCESSING);
        flowService.resolve(workOrder);
        assertEquals(WorkOrderStatus.RESOLVED, workOrder.getStatus());
    }

    @Test
    void shouldCloseWorkOrder() {
        workOrder.setStatus(WorkOrderStatus.RESOLVED);
        flowService.close(workOrder);
        assertEquals(WorkOrderStatus.CLOSED, workOrder.getStatus());
    }
}
