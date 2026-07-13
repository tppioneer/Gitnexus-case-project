package com.example.telecom.dispatch.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.api.PagedResult;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.dto.DispatchRequest;
import com.example.telecom.dispatch.dto.DispatchResponse;
import com.example.telecom.dispatch.dto.DispatchSummaryResponse;
import com.example.telecom.dispatch.service.DispatchOrchestrationService;
import com.example.telecom.dispatch.service.DispatchStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    @Autowired
    private DispatchOrchestrationService orchestrationService;

    @Autowired
    private DispatchStatisticsService statisticsService;

    private int errorCode(DomainException e) {
        try {
            return Integer.parseInt(e.getErrorCode());
        } catch (NumberFormatException ex) {
            return 500;
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<DispatchResponse>> assign(@RequestBody DispatchRequest request) {
        try {
            DispatchResponse response = orchestrationService.dispatch(request.getWorkOrderId());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @PostMapping("/batch-assign")
    public ResponseEntity<ApiResponse<Void>> batchAssign(@RequestBody List<DispatchRequest> requests) {
        try {
            List<String> workOrderIds = requests.stream()
                    .map(DispatchRequest::getWorkOrderId)
                    .collect(Collectors.toList());
            orchestrationService.batchDispatch(workOrderIds);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<DispatchResponse>> getOrder(@PathVariable String orderId) {
        try {
            DispatchResponse response = orchestrationService.getOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PagedResult<DispatchResponse>>> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String regionCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            PagedResult<DispatchResponse> paged = orchestrationService.listOrders(status, priority, regionCode, page, size);
            return ResponseEntity.ok(ApiResponse.success(paged));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable String orderId) {
        try {
            orchestrationService.cancelDispatch(orderId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }
}
