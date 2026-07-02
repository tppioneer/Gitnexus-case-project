package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.api.OperationResult;
import com.example.telecom.workorder.service.WorkOrderBulkActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workorders/bulk")
public class WorkOrderBulkActionController {

    private final WorkOrderBulkActionService bulkActionService;

    public WorkOrderBulkActionController(WorkOrderBulkActionService bulkActionService) {
        this.bulkActionService = bulkActionService;
    }

    @PostMapping("/assign")
    public ApiResponse<OperationResult> bulkAssign(@RequestBody List<String> workOrderIds) {
        return ApiResponse.success(bulkActionService.bulkAssign(workOrderIds));
    }

    @PostMapping("/close")
    public ApiResponse<OperationResult> bulkClose(@RequestBody List<String> workOrderIds) {
        return ApiResponse.success(bulkActionService.bulkClose(workOrderIds));
    }
}
