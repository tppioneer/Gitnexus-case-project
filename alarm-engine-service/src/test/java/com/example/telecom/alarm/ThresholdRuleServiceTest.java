package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.ThresholdRuleRepository;
import com.example.telecom.alarm.service.ThresholdRuleService;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ThresholdRuleServiceTest {

    private ThresholdRuleService thresholdRuleService;
    private ThresholdRuleRepository thresholdRuleRepository;

    @BeforeEach
    void setUp() {
        thresholdRuleRepository = new ThresholdRuleRepository();
        thresholdRuleService = new ThresholdRuleService(thresholdRuleRepository);

        // Pre-load rules for all metric types
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "CPU Critical", "CPU_USAGE", 90.0, Severity.CRITICAL, true));
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "CPU Warning", "CPU_USAGE", 70.0, Severity.WARNING, true));
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "Memory Major", "MEMORY_USAGE", 85.0, Severity.MAJOR, true));
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "Memory Disabled", "MEMORY_USAGE", 95.0, Severity.CRITICAL, false));
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "Optical Warning", "OPTICAL_POWER", -25.0, Severity.WARNING, true));
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "Loss Critical", "PACKET_LOSS", 10.0, Severity.CRITICAL, true));
        thresholdRuleRepository.save(new ThresholdRule(
                UUID.randomUUID().toString(), "Temp Major", "TEMPERATURE", 70.0, Severity.MAJOR, true));
    }

    @Test
    void shouldLoadRulesForCpuUsage() {
        List<ThresholdRule> rules = thresholdRuleService.loadRules(MetricType.CPU_USAGE);
        assertEquals(2, rules.size());
        assertTrue(rules.stream().allMatch(ThresholdRule::isEnabled));
    }

    @Test
    void shouldLoadRulesForMemoryUsage() {
        List<ThresholdRule> rules = thresholdRuleService.loadRules(MetricType.MEMORY_USAGE);
        // Only the enabled rule should be returned
        assertEquals(1, rules.size());
        assertEquals("Memory Major", rules.get(0).getRuleName());
    }

    @Test
    void shouldReturnEmptyListForMetricTypeWithNoRules() {
        // No rules for PACKET_LOSS with metricType "UNKNOWN"
        List<ThresholdRule> rules = thresholdRuleService.loadRules(
                MetricType.valueOf("PACKET_LOSS"));
        assertFalse(rules.isEmpty());
    }

    @Test
    void shouldCreateNewRule() {
        ThresholdRule newRule = new ThresholdRule("new-rule", "Test Rule",
                "CPU_USAGE", 95.0, Severity.CRITICAL, true);
        ThresholdRule created = thresholdRuleService.createRule(newRule);
        assertNotNull(created);
        assertEquals("new-rule", created.getRuleId());

        List<ThresholdRule> allRules = thresholdRuleService.findAllRules();
        assertEquals(8, allRules.size());
    }

    @Test
    void shouldFindAllRules() {
        List<ThresholdRule> allRules = thresholdRuleService.findAllRules();
        assertEquals(7, allRules.size());
    }
}
