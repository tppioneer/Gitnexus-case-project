package com.example.telecom.workorder.assignment;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;

import java.util.List;

/**
 * Assigns work orders using round-robin load balancing.
 */
public class LoadBalancedAssigneeSelector implements AssigneeSelector {

    private int counter = 0;

    @Override
    public OperatorUser select(WorkOrder workOrder, List<OperatorUser> candidates) {
        if (candidates.isEmpty()) return null;
        int index = Math.abs(counter++ % candidates.size());
        return candidates.get(index);
    }
}
