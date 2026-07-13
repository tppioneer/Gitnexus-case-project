package com.example.telecom.dispatch.config;

import com.example.telecom.dispatch.engine.DispatchEngine;
import com.example.telecom.dispatch.rule.DispatchRuleRegistry;
import com.example.telecom.dispatch.service.DispatchScoringService;
import com.example.telecom.dispatch.service.OperatorAvailabilityService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatchConfig {

    private final DispatchProperties dispatchProperties;

    public DispatchConfig(DispatchProperties dispatchProperties) {
        this.dispatchProperties = dispatchProperties;
    }

    @Bean
    public DispatchRuleRegistry dispatchRuleRegistry() {
        return new DispatchRuleRegistry();
    }

    @Bean
    public DispatchScoringService dispatchScoringService() {
        return new DispatchScoringService();
    }

    @Bean
    public OperatorAvailabilityService operatorAvailabilityService() {
        return new OperatorAvailabilityService();
    }

    @Bean
    public DispatchEngine dispatchEngine(DispatchRuleRegistry dispatchRuleRegistry,
                                         DispatchScoringService dispatchScoringService,
                                         OperatorAvailabilityService operatorAvailabilityService) {
        return new DispatchEngine(dispatchScoringService, operatorAvailabilityService, dispatchRuleRegistry);
    }
}
