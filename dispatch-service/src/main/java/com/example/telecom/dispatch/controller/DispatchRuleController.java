package com.example.telecom.dispatch.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.dto.DispatchRuleRequest;
import com.example.telecom.dispatch.dto.DispatchRuleResponse;
import com.example.telecom.dispatch.service.DispatchRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch/rules")
public class DispatchRuleController {

    @Autowired
    private DispatchRuleService ruleService;

    private int errorCode(DomainException e) {
        try {
            return Integer.parseInt(e.getErrorCode());
        } catch (NumberFormatException ex) {
            return 500;
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DispatchRuleResponse>>> listRules() {
        try {
            List<DispatchRuleResponse> rules = ruleService.listRules();
            return ResponseEntity.ok(ApiResponse.success(rules));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DispatchRuleResponse>> createRule(@RequestBody DispatchRuleRequest request) {
        try {
            DispatchRuleResponse response = ruleService.createRule(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<ApiResponse<DispatchRuleResponse>> updateRule(
            @PathVariable Long ruleId,
            @RequestBody DispatchRuleRequest request) {
        try {
            DispatchRuleResponse response = ruleService.updateRule(ruleId, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long ruleId) {
        try {
            ruleService.deleteRule(ruleId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderRules(@RequestBody List<String> ruleIds) {
        try {
            ruleService.reorderRules(ruleIds);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (DomainException e) {
            return ResponseEntity.ok(ApiResponse.error(errorCode(e), e.getMessage()));
        }
    }
}
