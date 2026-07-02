package com.example.telecom.workorder;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.workorder.assignment.*;
import com.example.telecom.workorder.consumer.AlarmEventConsumer;
import com.example.telecom.workorder.event.WorkOrderEventPublisher;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.*;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmEventConsumerTest {

    private AlarmEventConsumer consumer;
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
        AssigneeSelectorRegistry registry = new AssigneeSelectorRegistry(List.of(
                new LoadBalancedAssigneeSelector()));
        List<OperatorUser> operators = List.of(
                new OperatorUser("op-1", "Alice", "EAST", "111", "a@t.com", "expert"));
        WorkOrderAssignmentService assignmentService = new WorkOrderAssignmentService(
                registry, workOrderRepository, operators);
        WorkOrderFlowService flowService = new WorkOrderFlowService(
                assignmentService, stateMachine, new WorkOrderEventPublisher(bus));
        AutoWorkOrderService autoService = new AutoWorkOrderService(workOrderRepository, flowService);

        consumer = new AlarmEventConsumer(autoService);
    }

    @Test
    void shouldConsumeAlarmAndCreateWorkOrder() {
        AlarmEvent event = new AlarmEvent("e1", "a1", "dev-1", Severity.CRITICAL,
                com.example.telecom.common.alarm.AlarmStatus.OPEN, "EAST", System.currentTimeMillis());

        consumer.onAlarmCreated(event);
        assertEquals(1, workOrderRepository.findAll().size());
    }
}
