package com.example.telecom.workorder;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.workorder.assignment.*;
import com.example.telecom.workorder.event.WorkOrderEventPublisher;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.*;
import com.example.telecom.workorder.workflow.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderEscalationServiceTest {

    private WorkOrderEscalationService escalationService;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        WorkOrderStateMachine stateMachine = new WorkOrderStateMachine();
        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent e) {}
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent e) {}
            @Override public void publish(WorkOrderEvent e) {}
        };
        WorkOrderEventPublisher publisher = new WorkOrderEventPublisher(bus);
        WorkOrderRepository repo = new WorkOrderRepository();
        AssigneeSelectorRegistry registry = new AssigneeSelectorRegistry(List.of(
                new LoadBalancedAssigneeSelector()));
        List<OperatorUser> operators = List.of(
                new OperatorUser("op-1", "Alice", "EAST", "111", "a@t.com", "expert"));
        WorkOrderAssignmentService assignmentService = new WorkOrderAssignmentService(
                registry, repo, operators);
        WorkOrderFlowService flowService = new WorkOrderFlowService(
                assignmentService, stateMachine, publisher);

        List<EscalationPolicy> policies = List.of(
                new SeverityEscalationPolicy(), new SlaEscalationPolicy());
        escalationService = new WorkOrderEscalationService(policies, flowService);

        // Create an old work order that will trigger SLA escalation
        workOrder = new WorkOrder("wo-old-1", "a1", "d1", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Old WO", "desc", "EAST",
                System.currentTimeMillis() - 3600000); // 1 hour ago
        workOrder.setAssignee("op-1");
    }

    @Test
    void shouldEscalateOldProcessingWorkOrder() {
        escalationService.checkAndEscalate(workOrder);
        // For a CRITICAL priority work order created 1 hour ago, SLA should be breached
        assertEquals(WorkOrderStatus.ESCALATED, workOrder.getStatus());
    }

    @Test
    void shouldNotEscalateNonProcessingWorkOrder() {
        workOrder.setStatus(WorkOrderStatus.CREATED);
        escalationService.checkAndEscalate(workOrder);
        assertEquals(WorkOrderStatus.CREATED, workOrder.getStatus());
    }

    @Test
    void shouldNotEscalateRecentWorkOrder() {
        WorkOrder recentWo = new WorkOrder("wo-new-1", "a2", "d2", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "New WO", "desc", "EAST",
                System.currentTimeMillis()); // just created
        recentWo.setAssignee("op-1");

        escalationService.checkAndEscalate(recentWo);
        assertEquals(WorkOrderStatus.PROCESSING, recentWo.getStatus());
    }
}
