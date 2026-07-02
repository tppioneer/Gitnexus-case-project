package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;

public class WorkOrderSlaService {

    public long getSlaRemainingMs(WorkOrder workOrder) {
        long slaMs = getSlaMillis(workOrder.getPriority());
        long elapsed = System.currentTimeMillis() - workOrder.getCreatedTime();
        return Math.max(0, slaMs - elapsed);
    }

    public boolean isSlaBreached(WorkOrder workOrder) {
        return getSlaRemainingMs(workOrder) <= 0;
    }

    private long getSlaMillis(WorkOrderPriority priority) {
        return switch (priority) {
            case CRITICAL -> 15 * 60 * 1000L;
            case HIGH -> 60 * 60 * 1000L;
            case MEDIUM -> 4 * 60 * 60 * 1000L;
            case LOW -> 24 * 60 * 60 * 1000L;
        };
    }
}
