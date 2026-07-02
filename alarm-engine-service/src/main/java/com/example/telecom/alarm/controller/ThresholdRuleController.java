package com.example.telecom.alarm.controller;

import com.example.telecom.alarm.dto.ThresholdRuleRequest;
import com.example.telecom.alarm.service.ThresholdRuleService;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/threshold-rules")
public class ThresholdRuleController {

    private final ThresholdRuleService thresholdRuleService;

    public ThresholdRuleController(ThresholdRuleService thresholdRuleService) {
        this.thresholdRuleService = thresholdRuleService;
    }

    @GetMapping
    public ApiResponse<List<ThresholdRule>> listRules() {
        return ApiResponse.success(thresholdRuleService.findAllRules());
    }

    @PostMapping
    public ApiResponse<ThresholdRule> createRule(@RequestBody ThresholdRuleRequest request) {
        ThresholdRule rule = new ThresholdRule(
                UUID.randomUUID().toString(),
                request.getRuleName(),
                request.getMetricType(),
                request.getThresholdValue(),
                Severity.valueOf(request.getSeverity()),
                request.isEnabled()
        );
        return ApiResponse.success(thresholdRuleService.createRule(rule));
    }
}
