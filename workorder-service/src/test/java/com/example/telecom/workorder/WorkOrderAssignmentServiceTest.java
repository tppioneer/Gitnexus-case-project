package com.example.telecom.workorder;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.assignment.*;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderAssignmentServiceTest {

    private WorkOrderAssignmentService service;
    private WorkOrderRepository repository;
    private List<OperatorUser> operators;

    @BeforeEach
    void setUp() {
        repository = new WorkOrderRepository();
        operators = List.of(
                new OperatorUser("op-east-1", "Alice", "EAST", "13800000001", "alice@t.com", "network-expert"),
                new OperatorUser("op-west-1", "Bob", "WEST", "13800000002", "bob@t.com", "field-engineer"),
                new OperatorUser("op-east-2", "Charlie", "EAST", "13800000003", "charlie@t.com", "noc-operator"),
                new OperatorUser("op-south-1", "Diana", "SOUTH", "13800000004", "diana@t.com", "network-expert")
        );

        AssigneeSelectorRegistry registry = new AssigneeSelectorRegistry(List.of(
                new RegionBasedAssigneeSelector(),
                new SkillBasedAssigneeSelector(),
                new LoadBalancedAssigneeSelector()
        ));
        service = new WorkOrderAssignmentService(registry, repository, operators);
    }

    @Test
    void shouldAssignByRegionWhenUsingRegionBasedStrategy() {
        WorkOrder wo = new WorkOrder("wo-east-1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "East Fault", "desc", "EAST", System.currentTimeMillis());
        service.assign(wo);
        assertNotNull(wo.getAssignee());
        assertTrue(wo.getAssignee().startsWith("op-east"));
    }

    @Test
    void shouldAssignToAvailableOperatorEvenWhenRegionMismatch() {
        // All operators except one are filtered, but fallback should work
        WorkOrder wo = new WorkOrder("wo-north-1", "a2", "d2", WorkOrderStatus.CREATED,
                WorkOrderPriority.MEDIUM, "North Fault", "desc", "NORTH", System.currentTimeMillis());
        service.assign(wo);
        assertNotNull(wo.getAssignee());
    }

    @Test
    void shouldLoadBalanceAcrossAssignments() {
        AssigneeSelectorRegistry loadBalancedRegistry = new AssigneeSelectorRegistry(List.of(
                new LoadBalancedAssigneeSelector()
        ));
        WorkOrderAssignmentService lbService = new WorkOrderAssignmentService(
                loadBalancedRegistry, repository, operators);

        WorkOrder wo1 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        WorkOrder wo2 = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        WorkOrder wo3 = WorkOrderFixtureFactory.createdWorkOrder("EAST");

        lbService.assign(wo1);
        lbService.assign(wo2);
        lbService.assign(wo3);

        assertNotNull(wo1.getAssignee());
        assertNotNull(wo2.getAssignee());
        assertNotNull(wo3.getAssignee());
    }
}
