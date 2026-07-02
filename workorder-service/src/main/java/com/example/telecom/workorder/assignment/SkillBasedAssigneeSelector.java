package com.example.telecom.workorder.assignment;

import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;

import java.util.Comparator;
import java.util.List;

/**
 * Assigns work orders based on skill group matching.
 */
public class SkillBasedAssigneeSelector implements AssigneeSelector {

    @Override
    public OperatorUser select(WorkOrder workOrder, List<OperatorUser> candidates) {
        String requiredSkill = determineRequiredSkill(workOrder.getPriority());
        return candidates.stream()
                .filter(u -> requiredSkill.equals(u.getSkillGroup()))
                .min(Comparator.comparingInt(u -> u.getUserId().hashCode() % 10))
                .orElse(candidates.isEmpty() ? null : candidates.get(0));
    }

    private String determineRequiredSkill(WorkOrderPriority priority) {
        return switch (priority) {
            case CRITICAL, HIGH -> "network-expert";
            case MEDIUM -> "field-engineer";
            case LOW -> "noc-operator";
        };
    }
}
