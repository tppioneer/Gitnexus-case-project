package com.example.telecom.workorder.repository;

import com.example.telecom.common.workorder.WorkOrder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkOrderRepository {
    private final Map<String, WorkOrder> workOrders = new ConcurrentHashMap<>();

    public WorkOrder save(WorkOrder workOrder) {
        workOrders.put(workOrder.getWorkOrderId(), workOrder);
        return workOrder;
    }

    public Optional<WorkOrder> findById(String workOrderId) {
        return Optional.ofNullable(workOrders.get(workOrderId));
    }

    public List<WorkOrder> findByDeviceId(String deviceId) {
        return workOrders.values().stream()
                .filter(w -> deviceId.equals(w.getDeviceId()))
                .toList();
    }

    public List<WorkOrder> findByAssignee(String assignee) {
        return workOrders.values().stream()
                .filter(w -> assignee.equals(w.getAssignee()))
                .toList();
    }

    public List<WorkOrder> findAll() {
        return new ArrayList<>(workOrders.values());
    }
}
