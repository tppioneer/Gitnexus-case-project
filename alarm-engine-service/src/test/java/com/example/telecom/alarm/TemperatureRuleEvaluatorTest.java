package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.TemperatureRuleEvaluator;
import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureRuleEvaluatorTest {

    private final TemperatureRuleEvaluator evaluator = new TemperatureRuleEvaluator();

    @Test
    void shouldTriggerWhenTemperatureExceedsThreshold() {
        ThresholdRule rule = new ThresholdRule("r1", "High Temp", "TEMPERATURE", 65.0, Severity.MAJOR, true);
        DeviceMetric metric = new DeviceMetric("m1", "d1", MetricType.TEMPERATURE, 72.0,
                "°C", Instant.now(), "Huawei");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.isTriggered());
        assertEquals(Severity.MAJOR, result.getSeverity());
    }

    @Test
    void shouldNotTriggerWhenTemperatureNormal() {
        ThresholdRule rule = new ThresholdRule("r1", "Normal Temp", "TEMPERATURE", 65.0, Severity.MAJOR, true);
        DeviceMetric metric = new DeviceMetric("m2", "d2", MetricType.TEMPERATURE, 45.0,
                "°C", Instant.now(), "ZTE");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertFalse(result.isTriggered());
        assertNull(result.getSeverity());
    }

    @Test
    void shouldIncludeTemperatureInMessage() {
        ThresholdRule rule = new ThresholdRule("r3", "Temp", "TEMPERATURE", 70.0, Severity.CRITICAL, true);
        DeviceMetric metric = new DeviceMetric("m3", "d3", MetricType.TEMPERATURE, 82.5,
                "°C", Instant.now(), "FiberHome");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.getMessage().contains("Temperature"));
        assertTrue(result.getMessage().contains("82.5"));
        assertTrue(result.getMessage().contains("°C"));
    }

    @Test
    void shouldHandleExactThresholdBoundary() {
        ThresholdRule rule = new ThresholdRule("r4", "Boundary", "TEMPERATURE", 70.0, Severity.WARNING, true);

        DeviceMetric atThreshold = new DeviceMetric("m4", "d4", MetricType.TEMPERATURE, 70.0,
                "°C", Instant.now(), "Generic");
        assertFalse(evaluator.evaluate(atThreshold, rule).isTriggered());

        DeviceMetric justAbove = new DeviceMetric("m5", "d5", MetricType.TEMPERATURE, 70.01,
                "°C", Instant.now(), "Generic");
        assertTrue(evaluator.evaluate(justAbove, rule).isTriggered());
    }

    @Test
    void shouldHandleVeryLowTemperature() {
        ThresholdRule rule = new ThresholdRule("r5", "Low Temp", "TEMPERATURE", -20.0, Severity.WARNING, true);
        DeviceMetric metric = new DeviceMetric("m6", "d6", MetricType.TEMPERATURE, -35.0,
                "°C", Instant.now(), "Huawei");
        // Temperature is below threshold (freezing), but the evaluator checks > threshold
        assertFalse(evaluator.evaluate(metric, rule).isTriggered());
    }
}
