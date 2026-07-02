package com.example.telecom.workorder;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.workorder.assignment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssigneeSelectorRegistryTest {

    private AssigneeSelectorRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new AssigneeSelectorRegistry(List.of(
                new RegionBasedAssigneeSelector(),
                new SkillBasedAssigneeSelector(),
                new LoadBalancedAssigneeSelector()
        ));
    }

    @Test
    void shouldResolveRegionBasedSelector() {
        AssigneeSelector selector = registry.resolve("RegionBased");
        assertNotNull(selector);
        assertTrue(selector instanceof RegionBasedAssigneeSelector);
    }

    @Test
    void shouldResolveSkillBasedSelector() {
        AssigneeSelector selector = registry.resolve("SkillBased");
        assertNotNull(selector);
        assertTrue(selector instanceof SkillBasedAssigneeSelector);
    }

    @Test
    void shouldResolveLoadBalancedSelector() {
        AssigneeSelector selector = registry.resolve("LoadBalanced");
        assertNotNull(selector);
        assertTrue(selector instanceof LoadBalancedAssigneeSelector);
    }

    @Test
    void shouldFallbackToDefault() {
        AssigneeSelector selector = registry.resolve("UnknownStrategy");
        assertNotNull(selector);
    }

    @Test
    void allStrategiesShouldSelectAnOperator() {
        List<OperatorUser> operators = List.of(
                new OperatorUser("op-1", "Alice", "EAST", "111", "a@t.com", "network-expert"),
                new OperatorUser("op-2", "Bob", "WEST", "222", "b@t.com", "field-engineer")
        );
        WorkOrder wo = new WorkOrder("w1", "a1", "d1",
                com.example.telecom.common.workorder.WorkOrderStatus.CREATED,
                com.example.telecom.common.workorder.WorkOrderPriority.HIGH,
                "t", "d", "EAST", System.currentTimeMillis());

        // All selectors should return a non-null operator
        for (String strategy : List.of("RegionBased", "SkillBased", "LoadBalanced")) {
            AssigneeSelector selector = registry.resolve(strategy);
            OperatorUser selected = selector.select(wo, operators);
            assertNotNull(selected, "Strategy " + strategy + " should select an operator");
        }
    }
}
