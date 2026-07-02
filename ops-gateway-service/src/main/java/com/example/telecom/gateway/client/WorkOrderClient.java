package com.example.telecom.gateway.client;

import com.example.telecom.common.workorder.WorkOrder;

import java.util.List;

/**
 * Simulated client for workorder-service.
 */
public class WorkOrderClient {

    public List<WorkOrder> fetchOpenWorkOrderSummary() {
        return List.of();
    }

    public WorkOrder fetchWorkOrder(String workOrderId) {
        return null;
    }
}
