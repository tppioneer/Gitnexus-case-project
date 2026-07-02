package com.example.telecom.alarm;

import com.example.telecom.alarm.service.AlarmEscalationEvaluator;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AlarmEscalationEvaluator — another Case B NOISE evaluate().
 */
class AlarmEscalationEvaluatorTest {

    private AlarmEscalationEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new AlarmEscalationEvaluator(5, 15, 30);
    }

    @Test
    void shouldEscalateOldCriticalAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST",
                System.currentTimeMillis() - 10 * 60 * 1000L); // 10 min old
        assertTrue(evaluator.evaluate(alarm));
    }

    @Test
    void shouldNotEscalateRecentCriticalAlarm() {
        AlarmRecord alarm = new AlarmRecord("a2", "d2", "m2", "OPTICAL_POWER",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "WEST",
                System.currentTimeMillis()); // just created
        assertFalse(evaluator.evaluate(alarm));
    }

    @Test
    void shouldNotEscalateInfoAlarm() {
        AlarmRecord alarm = new AlarmRecord("a3", "d3", "m3", "TEMPERATURE",
                Severity.INFO, AlarmStatus.OPEN, "desc", "SOUTH",
                System.currentTimeMillis() - 3600000L); // 1 hour old
        assertFalse(evaluator.evaluate(alarm));
    }

    @Test
    void shouldEscalateOldMajorAlarm() {
        AlarmRecord alarm = new AlarmRecord("a4", "d4", "m4", "MEMORY_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "NORTH",
                System.currentTimeMillis() - 20 * 60 * 1000L); // 20 min old
        assertTrue(evaluator.evaluate(alarm));
    }

    @Test
    void shouldEscalateOldWarningAlarm() {
        AlarmRecord alarm = new AlarmRecord("a5", "d5", "m5", "PACKET_LOSS",
                Severity.WARNING, AlarmStatus.OPEN, "desc", "EAST",
                System.currentTimeMillis() - 35 * 60 * 1000L); // 35 min old
        assertTrue(evaluator.evaluate(alarm));
    }

    @Test
    void overloadedEvaluateWithInstantShouldReturnEscalateMessage() {
        AlarmRecord alarm = new AlarmRecord("a6", "d6", "m6", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", 0L);
        Instant referenceTime = Instant.ofEpochMilli(10 * 60 * 1000L);

        String result = evaluator.evaluate(alarm, referenceTime);
        assertTrue(result.contains("ESCALATE"));
        assertTrue(result.contains("a6"));
    }

    @Test
    void overloadedEvaluateShouldReturnOkWhenNotDue() {
        AlarmRecord alarm = new AlarmRecord("a7", "d7", "m7", "TEMPERATURE",
                Severity.WARNING, AlarmStatus.OPEN, "desc", "WEST",
                System.currentTimeMillis());
        Instant now = Instant.now();

        String result = evaluator.evaluate(alarm, now);
        assertTrue(result.contains("OK"));
    }
}
