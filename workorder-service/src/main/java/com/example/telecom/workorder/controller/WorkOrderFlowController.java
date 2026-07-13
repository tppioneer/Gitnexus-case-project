package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.dto.WorkOrderResponse;
import com.example.telecom.workorder.dto.WorkOrderTransitionRequest;
import com.example.telecom.workorder.mapper.WorkOrderMapper;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderFlowService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workorders")
public class WorkOrderFlowController {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderFlowService workOrderFlowService;
    private final WorkOrderMapper workOrderMapper;

    public WorkOrderFlowController(WorkOrderRepository workOrderRepository,
                                    WorkOrderFlowService workOrderFlowService,
                                    WorkOrderMapper workOrderMapper) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderFlowService = workOrderFlowService;
        this.workOrderMapper = workOrderMapper;
    }

    @PostMapping("/{workOrderId}/transition")
    public ApiResponse<WorkOrderResponse> transition(@PathVariable String workOrderId,
                                                       @RequestBody WorkOrderTransitionRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderId));
        WorkOrderStatus target = WorkOrderStatus.valueOf(request.getTargetStatus());

        switch (target) {
            case ASSIGNED -> workOrderFlowService.assign(workOrder);
            case PROCESSING -> workOrderFlowService.startProcessing(workOrder);
            case RESOLVED -> workOrderFlowService.resolve(workOrder);
            case CLOSED -> workOrderFlowService.close(workOrder);
            case ESCALATED -> workOrderFlowService.escalate(workOrder);
            case CANCELLED -> workOrderFlowService.cancel(workOrder);
            case WAITING_VENDOR -> workOrderFlowService.waitForVendor(workOrder);
            default -> throw new IllegalArgumentException("Cannot transition to: " + target);
        }

        return ApiResponse.success(workOrderMapper.toResponse(workOrder));
    }
}
