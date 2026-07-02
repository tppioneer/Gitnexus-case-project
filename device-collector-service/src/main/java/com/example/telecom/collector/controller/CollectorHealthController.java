package com.example.telecom.collector.controller;

import com.example.telecom.collector.config.CollectorProperties;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CollectorHealthController {

    private final CollectorProperties collectorProperties;

    public CollectorHealthController(CollectorProperties collectorProperties) {
        this.collectorProperties = collectorProperties;
    }

    @GetMapping("/health/collector")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
                "status", "UP",
                "validationEnabled", collectorProperties.isValidationEnabled()
        ));
    }
}
