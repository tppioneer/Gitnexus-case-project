package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.MemoryUsageRuleEvaluator;
import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUsageRuleEvaluatorTest {

    @Test
    void shouldTriggerWhenMemoryExceedsThreshold() {
        MemoryUsageRuleEvaluator evaluator = new MemoryUsageRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "Memory High", "MEMORY_USAGE", 85.0, Severity.MAJOR, true);
        DeviceMetric metric = new DeviceMetric("m1", "d1", MetricType.MEMORY_USAGE, 92.0,
                "%", Instant.now(), "Huawei");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.isTriggered());
        assertEquals(Severity.MAJOR, result.getSeverity());
    }

    @Test
    void shouldNotTriggerWhenMemoryNormal() {
        MemoryUsageRuleEvaluator evaluator = new MemoryUsageRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "Memory Normal", "MEMORY_USAGE", 85.0, Severity.MAJOR, true);
        DeviceMetric metric = new DeviceMetric("m2", "d2", MetricType.MEMORY_USAGE, 60.0,
                "%", Instant.now(), "ZTE");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertFalse(result.isTriggered());
    }

    @Test
    void shouldTriggerExactlyAtThreshold() {
        MemoryUsageRuleEvaluator evaluator = new MemoryUsageRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "Memory", "MEMORY_USAGE", 85.0, Severity.WARNING, true);
        DeviceMetric metric = new DeviceMetric("m3", "d3", MetricType.MEMORY_USAGE, 85.01,
                "%", Instant.now(), "FiberHome");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.isTriggered());
    }

    @Test
    void shouldContainMetricInfoInMessage() {
        MemoryUsageRuleEvaluator evaluator = new MemoryUsageRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "Mem", "MEMORY_USAGE", 80.0, Severity.WARNING, true);
        DeviceMetric metric = new DeviceMetric("m4", "d4", MetricType.MEMORY_USAGE, 95.0,
                "%", Instant.now(), "Generic");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.getMessage().contains("Memory"));
        assertTrue(result.getMessage().contains("95"));
        assertTrue(result.getMessage().contains("80"));
    }

    @Test
    void shouldReturnCorrectThresholdAndCurrentValue() {
        MemoryUsageRuleEvaluator evaluator = new MemoryUsageRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r5", "Mem", "MEMORY_USAGE", 75.0, Severity.MAJOR, true);
        DeviceMetric metric = new DeviceMetric("m5", "d5", MetricType.MEMORY_USAGE, 88.5,
                "%", Instant.now(), "Huawei");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertEquals(88.5, result.getCurrentValue(), 0.01);
        assertEquals(75.0, result.getThresholdValue(), 0.01);
    }
}
