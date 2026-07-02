package com.example.telecom.alarm;

import com.example.telecom.alarm.service.RiskEvaluator;
import com.example.telecom.alarm.service.SlaEvaluator;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SlaEvaluator and RiskEvaluator.
 * These are Case B NOISE items — they have evaluate() methods
 * but do NOT implement RuleEvaluator.
 */
class SlaRiskEvaluatorNoiseTest {

    private SlaEvaluator slaEvaluator;
    private RiskEvaluator riskEvaluator;

    @BeforeEach
    void setUp() {
        slaEvaluator = new SlaEvaluator();
        riskEvaluator = new RiskEvaluator();
    }

    @Test
    void slaEvaluatorShouldEvaluateCompliantAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        // Recent alarm should be SLA compliant
        assertTrue(slaEvaluator.evaluate(alarm, 3600000L));
    }

    @Test
    void slaEvaluatorShouldEvaluateNonCompliantAlarm() {
        AlarmRecord alarm = new AlarmRecord("a2", "d2", "m2", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "WEST",
                System.currentTimeMillis() - 7200000L); // 2 hours old
        assertFalse(slaEvaluator.evaluate(alarm, 60000L)); // 1 min SLA
    }

    @Test
    void slaEvaluatorStringOverloadShouldReturnMessage() {
        String result = slaEvaluator.evaluate("alarm-001");
        assertTrue(result.contains("alarm-001"));
        assertTrue(result.contains("compliant"));
    }

    @Test
    void riskEvaluatorShouldScoreCriticalAs100() {
        AlarmRecord alarm = createAlarm(Severity.CRITICAL);
        assertEquals(100, riskEvaluator.evaluate(alarm));
    }

    @Test
    void riskEvaluatorShouldScoreMajorAs70() {
        AlarmRecord alarm = createAlarm(Severity.MAJOR);
        assertEquals(70, riskEvaluator.evaluate(alarm));
    }

    @Test
    void riskEvaluatorShouldScoreWarningAs40() {
        AlarmRecord alarm = createAlarm(Severity.WARNING);
        assertEquals(40, riskEvaluator.evaluate(alarm));
    }

    @Test
    void riskEvaluatorShouldScoreInfoAs10() {
        AlarmRecord alarm = createAlarm(Severity.INFO);
        assertEquals(10, riskEvaluator.evaluate(alarm));
    }

    @Test
    void riskEvaluatorRegionOverloadShouldReturnMessage() {
        String result = riskEvaluator.evaluate("EAST", 5);
        assertTrue(result.contains("EAST"));
        assertTrue(result.contains("50%"));
    }

    @Test
    void riskEvaluatorRegionOverloadShouldCapAt100Percent() {
        String result = riskEvaluator.evaluate("WEST", 20);
        assertTrue(result.contains("100%"));
    }

    private AlarmRecord createAlarm(Severity severity) {
        return new AlarmRecord("a-test", "d-test", "m-test", "CPU_USAGE",
                severity, AlarmStatus.OPEN, "test alarm", "REGION", System.currentTimeMillis());
    }
}
