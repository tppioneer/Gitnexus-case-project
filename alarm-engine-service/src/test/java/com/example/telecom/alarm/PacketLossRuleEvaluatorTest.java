package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.PacketLossRuleEvaluator;
import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PacketLossRuleEvaluatorTest {

    @Test
    void shouldTriggerWhenPacketLossExceedsThreshold() {
        PacketLossRuleEvaluator evaluator = new PacketLossRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "High Loss", "PACKET_LOSS", 5.0, Severity.CRITICAL, true);
        DeviceMetric metric = new DeviceMetric("m1", "dev-1", MetricType.PACKET_LOSS, 12.5,
                "%", Instant.now(), "FiberHome");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertTrue(result.isTriggered());
        assertEquals(Severity.CRITICAL, result.getSeverity());
    }

    @Test
    void shouldNotTriggerWhenPacketLossBelowThreshold() {
        PacketLossRuleEvaluator evaluator = new PacketLossRuleEvaluator();
        ThresholdRule rule = new ThresholdRule("r1", "Normal Loss", "PACKET_LOSS", 5.0, Severity.CRITICAL, true);
        DeviceMetric metric = new DeviceMetric("m2", "dev-2", MetricType.PACKET_LOSS, 0.5,
                "%", Instant.now(), "Generic");

        EvaluationResult result = evaluator.evaluate(metric, rule);
        assertFalse(result.isTriggered());
    }
}
