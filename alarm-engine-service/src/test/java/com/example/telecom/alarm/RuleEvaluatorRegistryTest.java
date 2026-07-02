package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.*;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleEvaluatorRegistryTest {

    private RuleEvaluatorRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new RuleEvaluatorRegistry(List.of(
                new CpuUsageRuleEvaluator(),
                new MemoryUsageRuleEvaluator(),
                new OpticalPowerRuleEvaluator(),
                new PacketLossRuleEvaluator(),
                new TemperatureRuleEvaluator()
        ));
    }

    @Test
    void shouldResolveCpuEvaluator() {
        RuleEvaluator evaluator = registry.resolve(MetricType.CPU_USAGE);
        assertNotNull(evaluator);
        assertTrue(evaluator instanceof CpuUsageRuleEvaluator);
    }

    @Test
    void shouldResolveOpticalPowerEvaluator() {
        RuleEvaluator evaluator = registry.resolve(MetricType.OPTICAL_POWER);
        assertNotNull(evaluator);
        assertTrue(evaluator instanceof OpticalPowerRuleEvaluator);
    }

    @Test
    void shouldResolvePacketLossEvaluator() {
        RuleEvaluator evaluator = registry.resolve(MetricType.PACKET_LOSS);
        assertNotNull(evaluator);
        assertTrue(evaluator instanceof PacketLossRuleEvaluator);
    }

    @Test
    void shouldResolveAllMetricTypes() {
        for (MetricType type : MetricType.values()) {
            assertNotNull(registry.resolve(type), "No evaluator for " + type);
        }
    }
}
