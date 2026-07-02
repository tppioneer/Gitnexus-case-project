package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.ThresholdRule;

import java.util.List;

public class ThresholdRuleService {

    private final com.example.telecom.alarm.repository.ThresholdRuleRepository thresholdRuleRepository;

    public ThresholdRuleService(com.example.telecom.alarm.repository.ThresholdRuleRepository thresholdRuleRepository) {
        this.thresholdRuleRepository = thresholdRuleRepository;
    }

    public List<ThresholdRule> loadRules(com.example.telecom.common.device.MetricType metricType) {
        return thresholdRuleRepository.loadRules(metricType);
    }

    public ThresholdRule createRule(ThresholdRule rule) {
        return thresholdRuleRepository.save(rule);
    }

    public List<ThresholdRule> findAllRules() {
        return thresholdRuleRepository.findAll();
    }
}
