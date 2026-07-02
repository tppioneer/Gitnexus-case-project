package com.example.telecom.workorder;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.alarm.Severity;
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

class AutoWorkOrderServiceTest {

    private AutoWorkOrderService autoWorkOrderService;
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
        WorkOrderEventPublisher eventPublisher = new WorkOrderEventPublisher(bus);

        List<OperatorUser> operators = List.of(
                new OperatorUser("op-1", "Alice", "EAST", "13800000001", "a@t.com", "network-expert")
        );
        AssigneeSelectorRegistry selectorRegistry = new AssigneeSelectorRegistry(List.of(
                new RegionBasedAssigneeSelector(), new SkillBasedAssigneeSelector(),
                new LoadBalancedAssigneeSelector()
        ));
        WorkOrderAssignmentService assignmentService = new WorkOrderAssignmentService(
                selectorRegistry, workOrderRepository, operators);
        WorkOrderFlowService flowService = new WorkOrderFlowService(
                assignmentService, stateMachine, eventPublisher);

        autoWorkOrderService = new AutoWorkOrderService(workOrderRepository, flowService);
    }

    @Test
    void shouldCreateWorkOrderForCriticalAlarm() {
        AlarmEvent event = new AlarmEvent("e1", "a1", "dev-1", Severity.CRITICAL,
                com.example.telecom.common.alarm.AlarmStatus.OPEN, "EAST", System.currentTimeMillis());

        WorkOrder wo = autoWorkOrderService.createForAlarm(event);
        assertNotNull(wo);
        assertEquals(WorkOrderStatus.ASSIGNED, wo.getStatus());
        // Case C: AlarmEvent.deviceRegionCode → WorkOrder.maintenanceRegionCode
        assertEquals("EAST", wo.getMaintenanceRegionCode());
    }

    @Test
    void shouldNotCreateWorkOrderForInfoAlarm() {
        AlarmEvent event = new AlarmEvent("e2", "a2", "dev-2", Severity.INFO,
                com.example.telecom.common.alarm.AlarmStatus.OPEN, "WEST", System.currentTimeMillis());

        WorkOrder wo = autoWorkOrderService.createForAlarm(event);
        assertNull(wo);
    }
}
