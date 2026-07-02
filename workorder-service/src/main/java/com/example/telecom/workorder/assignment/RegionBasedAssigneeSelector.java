package com.example.telecom.workorder.assignment;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;

import java.util.List;

/**
 * Assigns work orders based on matching region code.
 */
public class RegionBasedAssigneeSelector implements AssigneeSelector {

    @Override
    public OperatorUser select(WorkOrder workOrder, List<OperatorUser> candidates) {
        return candidates.stream()
                .filter(u -> workOrder.getMaintenanceRegionCode() != null
                        && workOrder.getMaintenanceRegionCode().equals(u.getRegionCode()))
                .findFirst()
                .orElse(candidates.isEmpty() ? null : candidates.get(0));
    }
}
