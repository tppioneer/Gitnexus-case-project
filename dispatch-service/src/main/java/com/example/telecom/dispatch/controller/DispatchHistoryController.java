package com.example.telecom.dispatch.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.domain.DispatchHistory;
import com.example.telecom.dispatch.service.DispatchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dispatch/history")
public class DispatchHistoryController {

    @Autowired
    private DispatchHistoryService historyService;

    private int errorCode(DomainException e) {
        try {
            return Integer.parseInt(e.getErrorCode());
        } catch (NumberFormatException ex) {
            return 500;
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DispatchHistory>>> listHistory(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<DispatchHistory> history = historyService.getRecentDispatch(limit);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<ApiResponse<List<DispatchHistory>>> getByOperator(@PathVariable String operatorId) {
        try {
            List<DispatchHistory> history = historyService.getByOperator(operatorId);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<DispatchHistory>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime to) {
        try {
            List<DispatchHistory> history = historyService.getByDateRange(from, to);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }
}
