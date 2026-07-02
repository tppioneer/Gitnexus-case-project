package com.example.telecom.gateway;

import com.example.telecom.gateway.service.DashboardHealthEvaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DashboardHealthEvaluator — Case B NOISE item in ops-gateway-service.
 * Has evaluate() methods but does NOT implement RuleEvaluator.
 */
class DashboardHealthEvaluatorTest {

    private final DashboardHealthEvaluator evaluator = new DashboardHealthEvaluator();

    @Test
    void shouldEvaluateRegionHealth() {
        String result = evaluator.evaluate("EAST");
        assertTrue(result.contains("EAST"));
    }

    @Test
    void shouldReturnHealthyForDifferentRegions() {
        assertTrue(evaluator.evaluate("EAST").contains("HEALTHY"));
        assertTrue(evaluator.evaluate("WEST").contains("HEALTHY"));
        assertTrue(evaluator.evaluate("SOUTH").contains("HEALTHY"));
    }

    @Test
    void shouldEvaluateOverloadedMethod() {
        // Healthy: few alarms relative to devices
        assertEquals(100, evaluator.evaluate(10, 5));

        // Warning: moderate alarms
        assertEquals(50, evaluator.evaluate(10, 12));

        // Critical: many alarms
        assertEquals(0, evaluator.evaluate(5, 15));
    }

    @Test
    void overloadedEvaluateShouldHandleZeroDevices() {
        int result = evaluator.evaluate(0, 5);
        assertEquals(0, result);
    }

    @Test
    void overloadedEvaluateShouldHandleZeroAlarms() {
        int result = evaluator.evaluate(10, 0);
        assertEquals(100, result);
    }
}
