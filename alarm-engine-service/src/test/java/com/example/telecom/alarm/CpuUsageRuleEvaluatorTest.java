package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.CpuUsageRuleEvaluator;
import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CpuUsageRuleEvaluatorTest {

    private CpuUsageRuleEvaluator evaluator;
    private ThresholdRule rule;
    private DeviceMetric metric;

    @BeforeEach
    void setUp() {
        evaluator = new CpuUsageRuleEvaluator();
        rule = new ThresholdRule("rule-1", "CPU High", "CPU_USAGE", 80.0, Severity.MAJOR, true);
        metric = new DeviceMetric("m1", "dev-1", MetricType.CPU_USAGE, 90.0, "%",
                Instant.now(), "Huawei");
    }

    @Test
    void shouldTriggerAlarmWhenValueExceedsThreshold() {
        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.isTriggered());
        assertEquals(Severity.MAJOR, result.getSeverity());
        assertTrue(result.getMessage().contains("exceeds threshold"));
    }

    @Test
    void shouldNotTriggerAlarmWhenValueBelowThreshold() {
        metric.setValue(50.0);
        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertFalse(result.isTriggered());
        assertNull(result.getSeverity());
    }

    @Test
    void shouldEvaluateDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo("dev-1", "BS-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-1", "10.0.0.1", true);
        // This is the overloaded method — NOT part of RuleEvaluator interface
        boolean result = evaluator.evaluate(deviceInfo);
        assertTrue(result);
    }

    @Test
    void overloadedEvaluateShouldReturnFalseForNull() {
        // Overloaded evaluate(DeviceInfo) — Case B signature noise
        boolean result = evaluator.evaluate((DeviceInfo) null);
        assertFalse(result);
    }
}
