package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.dto.WorkOrderCreateRequest;
import com.example.telecom.workorder.dto.WorkOrderResponse;
import com.example.telecom.workorder.dto.WorkOrderTransitionRequest;
import com.example.telecom.workorder.mapper.WorkOrderMapper;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderFlowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderFlowService workOrderFlowService;
    private final WorkOrderMapper workOrderMapper;

    public WorkOrderController(WorkOrderRepository workOrderRepository,
                                WorkOrderFlowService workOrderFlowService,
                                WorkOrderMapper workOrderMapper) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderFlowService = workOrderFlowService;
        this.workOrderMapper = workOrderMapper;
    }

    @GetMapping
    public ApiResponse<List<WorkOrderResponse>> listWorkOrders() {
        return ApiResponse.success(workOrderRepository.findAll().stream()
                .map(workOrderMapper::toResponse).toList());
    }

    @GetMapping("/{workOrderId}")
    public ApiResponse<WorkOrderResponse> getWorkOrder(@PathVariable String workOrderId) {
        return workOrderRepository.findById(workOrderId)
                .map(workOrderMapper::toResponse)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Work order not found"));
    }

    @PostMapping
    public ApiResponse<WorkOrderResponse> createWorkOrder(@RequestBody WorkOrderCreateRequest request) {
        WorkOrder workOrder = new WorkOrder(
                UUID.randomUUID().toString(),
                request.getAlarmId(),
                request.getDeviceId(),
                WorkOrderStatus.CREATED,
                com.example.telecom.common.workorder.WorkOrderPriority.valueOf(request.getPriority()),
                request.getTitle(),
                request.getDescription(),
                null,
                System.currentTimeMillis()
        );
        workOrderRepository.save(workOrder);
        return ApiResponse.success(workOrderMapper.toResponse(workOrder));
    }
}
