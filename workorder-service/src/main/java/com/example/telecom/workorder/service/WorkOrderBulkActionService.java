package com.example.telecom.workorder.service;

import com.example.telecom.common.api.OperationResult;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.List;

public class WorkOrderBulkActionService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderFlowService workOrderFlowService;

    public WorkOrderBulkActionService(WorkOrderRepository workOrderRepository,
                                       WorkOrderFlowService workOrderFlowService) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderFlowService = workOrderFlowService;
    }

    public OperationResult bulkAssign(List<String> workOrderIds) {
        OperationResult result = new OperationResult("bulkAssign", workOrderIds.size());
        for (String id : workOrderIds) {
            workOrderRepository.findById(id).ifPresentOrElse(wo -> {
                try {
                    workOrderFlowService.assign(wo);
                    result.recordSuccess();
                } catch (Exception e) {
                    result.recordFailure("Failed to assign " + id + ": " + e.getMessage());
                }
            }, () -> result.recordFailure("Work order not found: " + id));
        }
        result.complete();
        return result;
    }

    public OperationResult bulkClose(List<String> workOrderIds) {
        OperationResult result = new OperationResult("bulkClose", workOrderIds.size());
        for (String id : workOrderIds) {
            workOrderRepository.findById(id).ifPresentOrElse(wo -> {
                if (wo.getStatus() == WorkOrderStatus.RESOLVED) {
                    try {
                        workOrderFlowService.close(wo);
                        result.recordSuccess();
                    } catch (Exception e) {
                        result.recordFailure("Failed to close " + id + ": " + e.getMessage());
                    }
                } else {
                    result.recordFailure("Work order " + id + " is not in RESOLVED status");
                }
            }, () -> result.recordFailure("Work order not found: " + id));
        }
        result.complete();
        return result;
    }

    public void process(List<WorkOrder> workOrders) {
        for (WorkOrder wo : workOrders) {
            if (wo.getStatus() == WorkOrderStatus.CREATED) {
                workOrderFlowService.assign(wo);
            }
        }
    }
}
