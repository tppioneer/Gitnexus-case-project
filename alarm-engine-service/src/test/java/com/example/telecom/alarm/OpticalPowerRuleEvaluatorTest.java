package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.OpticalPowerRuleEvaluator;
import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OpticalPowerRuleEvaluatorTest {

    @Test
    void shouldTriggerWhenOpticalPowerBelowThreshold() {
        OpticalPowerRuleEvaluator evaluator = new OpticalPowerRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "Low Optical", "OPTICAL_POWER", -20.0, Severity.WARNING, true);
        DeviceMetric metric = new DeviceMetric("m1", "dev-1", MetricType.OPTICAL_POWER, -25.0,
                "dBm", Instant.now(), "ZTE");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.isTriggered());
    }

    @Test
    void shouldNotTriggerWhenOpticalPowerAboveThreshold() {
        OpticalPowerRuleEvaluator evaluator = new OpticalPowerRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r2", "Normal Optical", "OPTICAL_POWER", -20.0, Severity.WARNING, true);
        DeviceMetric metric = new DeviceMetric("m2", "dev-2", MetricType.OPTICAL_POWER, -10.0,
                "dBm", Instant.now(), "Huawei");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertFalse(result.isTriggered());
    }
}
