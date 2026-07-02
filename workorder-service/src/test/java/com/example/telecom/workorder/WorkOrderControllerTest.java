package com.example.telecom.workorder;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.workorder.assignment.*;
import com.example.telecom.workorder.controller.WorkOrderController;
import com.example.telecom.workorder.dto.*;
import com.example.telecom.workorder.event.WorkOrderEventPublisher;
import com.example.telecom.workorder.mapper.WorkOrderMapper;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.*;
import com.example.telecom.workorder.workflow.WorkOrderStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderControllerTest {

    private WorkOrderController controller;
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
        WorkOrderMapper mapper = new WorkOrderMapper();

        controller = new WorkOrderController(workOrderRepository, flowService, mapper);
    }

    @Test
    void shouldListWorkOrders() {
        ApiResponse<List<WorkOrderResponse>> response = controller.listWorkOrders();
        assertEquals(200, response.getCode());
    }

    @Test
    void shouldReturn404ForMissingWorkOrder() {
        ApiResponse<WorkOrderResponse> response = controller.getWorkOrder("nonexistent");
        assertEquals(404, response.getCode());
    }
}
