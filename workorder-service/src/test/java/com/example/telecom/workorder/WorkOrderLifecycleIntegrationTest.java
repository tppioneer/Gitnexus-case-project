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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test covering the complete work order lifecycle from creation to closure.
 */
class WorkOrderLifecycleIntegrationTest {

    private WorkOrderRepository workOrderRepository;
    private WorkOrderFlowService flowService;
    private WorkOrderEscalationService escalationService;
    private List<WorkOrderEvent> publishedEvents;

    @BeforeEach
    void setUp() {
        workOrderRepository = new WorkOrderRepository();
        WorkOrderStateMachine stateMachine = new WorkOrderStateMachine();
        publishedEvents = new ArrayList<>();

        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent e) {}
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent e) {}
            @Override public void publish(WorkOrderEvent e) { publishedEvents.add(e); }
        };
        WorkOrderEventPublisher publisher = new WorkOrderEventPublisher(bus);

        List<OperatorUser> operators = List.of(
                new OperatorUser("op-east-1", "Alice", "EAST", "111", "alice@t.com", "network-expert"),
                new OperatorUser("op-east-2", "Bob", "EAST", "222", "bob@t.com", "field-engineer")
        );
        AssigneeSelectorRegistry registry = new AssigneeSelectorRegistry(List.of(
                new RegionBasedAssigneeSelector(), new SkillBasedAssigneeSelector(),
                new LoadBalancedAssigneeSelector()
        ));
        WorkOrderAssignmentService assignmentService = new WorkOrderAssignmentService(
                registry, workOrderRepository, operators);
        flowService = new WorkOrderFlowService(assignmentService, stateMachine, publisher);

        List<EscalationPolicy> policies = List.of(
                new SeverityEscalationPolicy(), new SlaEscalationPolicy());
        escalationService = new WorkOrderEscalationService(policies, flowService);
    }

    @Test
    void shouldCompleteFullLifecycle() {
        // Create
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo.setWorkOrderId("wo-lifecycle-1");
        workOrderRepository.save(wo);

        // Assign
        flowService.assign(wo);
        assertEquals(WorkOrderStatus.ASSIGNED, wo.getStatus());
        assertNotNull(wo.getAssignee());

        // Start processing
        flowService.startProcessing(wo);
        assertEquals(WorkOrderStatus.PROCESSING, wo.getStatus());

        // Resolve
        flowService.resolve(wo);
        assertEquals(WorkOrderStatus.RESOLVED, wo.getStatus());

        // Close
        flowService.close(wo);
        assertEquals(WorkOrderStatus.CLOSED, wo.getStatus());

        // Should have events for each transition: CREATED→ASSIGNED→PROCESSING→RESOLVED→CLOSED
        assertEquals(4, publishedEvents.size());
    }

    @Test
    void shouldCompleteEscalationLifecycle() {
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo.setWorkOrderId("wo-escalation-1");
        wo.setStatus(WorkOrderStatus.PROCESSING);
        wo.setPriority(WorkOrderPriority.CRITICAL);
        workOrderRepository.save(wo);

        // Should escalate due to old creation time (using fixture's current time)
        flowService.escalate(wo);
        assertEquals(WorkOrderStatus.ESCALATED, wo.getStatus());

        // Escalate back to processing (回路)
        flowService.startProcessing(wo);
        assertEquals(WorkOrderStatus.PROCESSING, wo.getStatus());
    }

    @Test
    void shouldCancelWorkOrder() {
        WorkOrder wo = WorkOrderFixtureFactory.assignedWorkOrder("WEST", "op-west-1");
        wo.setWorkOrderId("wo-cancel-1");
        workOrderRepository.save(wo);

        flowService.cancel(wo);
        assertEquals(WorkOrderStatus.CANCELLED, wo.getStatus());

        // Cannot transition from cancelled
        assertThrows(com.example.telecom.common.exception.ValidationException.class,
                () -> flowService.startProcessing(wo));
    }

    @Test
    void shouldRejectSkipStepTransition() {
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo.setWorkOrderId("wo-skip-1");
        workOrderRepository.save(wo);

        // Cannot go directly from CREATED to RESOLVED
        assertThrows(com.example.telecom.common.exception.ValidationException.class,
                () -> flowService.resolve(wo));
    }

    @Test
    void shouldPublishEventOnEachTransition() {
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo.setWorkOrderId("wo-events-1");
        workOrderRepository.save(wo);

        int beforeAssign = publishedEvents.size();
        flowService.assign(wo);
        assertEquals(beforeAssign + 1, publishedEvents.size());
        assertEquals(WorkOrderStatus.ASSIGNED, publishedEvents.get(beforeAssign).getToStatus());

        int beforeProcess = publishedEvents.size();
        flowService.startProcessing(wo);
        assertEquals(beforeProcess + 1, publishedEvents.size());
    }
}
