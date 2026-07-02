package com.example.telecom.workorder;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.assignment.RegionBasedAssigneeSelector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegionBasedAssigneeSelectorTest {

    @Test
    void shouldSelectMatchingRegionOperator() {
        RegionBasedAssigneeSelector selector = new RegionBasedAssigneeSelector();
        WorkOrder wo = new WorkOrder("wo-1", "a1", "dev-1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "Test", "desc", "EAST", System.currentTimeMillis());

        OperatorUser eastOp = new OperatorUser("op-1", "Alice", "EAST", "111", "a@t.com", "expert");
        OperatorUser westOp = new OperatorUser("op-2", "Bob", "WEST", "222", "b@t.com", "expert");

        OperatorUser selected = selector.select(wo, List.of(eastOp, westOp));
        assertEquals("op-1", selected.getUserId());
    }
}
