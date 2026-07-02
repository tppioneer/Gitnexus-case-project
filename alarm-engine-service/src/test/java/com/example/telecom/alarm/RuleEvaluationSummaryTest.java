package com.example.telecom.alarm;

import com.example.telecom.alarm.rule.RuleEvaluationSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleEvaluationSummaryTest {

    private RuleEvaluationSummary summary;

    @BeforeEach
    void setUp() {
        summary = new RuleEvaluationSummary();
    }

    @Test
    void shouldRecordTriggeredEvaluation() {
        summary.recordEvaluation("rule-cpu-1", "CPU_USAGE", true);
        assertEquals(1, summary.getTotalEvaluations());
        assertEquals(1, summary.getTriggeredEvaluations());
        assertEquals(0, summary.getNormalEvaluations());
    }

    @Test
    void shouldRecordNormalEvaluation() {
        summary.recordEvaluation("rule-cpu-1", "CPU_USAGE", false);
        assertEquals(1, summary.getNormalEvaluations());
        assertEquals(0, summary.getTriggeredEvaluations());
    }

    @Test
    void shouldCalculateTriggerRate() {
        summary.recordEvaluation("r1", "CPU_USAGE", true);
        summary.recordEvaluation("r1", "CPU_USAGE", true);
        summary.recordEvaluation("r2", "MEMORY_USAGE", false);
        summary.recordEvaluation("r3", "OPTICAL_POWER", false);
        summary.recordEvaluation("r4", "PACKET_LOSS", false);

        assertEquals(40.0, summary.getTriggerRate(), 0.01);
    }

    @Test
    void shouldFindMostTriggeredRule() {
        summary.recordEvaluation("r1", "CPU", true);
        summary.recordEvaluation("r1", "CPU", true);
        summary.recordEvaluation("r2", "CPU", true);
        summary.recordEvaluation("r3", "CPU", true);
        summary.recordEvaluation("r1", "CPU", true);
        summary.recordEvaluation("r2", "CPU", true);

        assertEquals("r1", summary.getMostTriggeredRule());
    }

    @Test
    void shouldFindMostTriggeredMetricType() {
        summary.recordEvaluation("r1", "CPU_USAGE", true);
        summary.recordEvaluation("r2", "CPU_USAGE", true);
        summary.recordEvaluation("r3", "CPU_USAGE", true);
        summary.recordEvaluation("r4", "MEMORY_USAGE", true);
        summary.recordEvaluation("r5", "MEMORY_USAGE", true);
        summary.recordEvaluation("r6", "OPTICAL_POWER", true);

        assertEquals("CPU_USAGE", summary.getMostTriggeredMetricType());
    }

    @Test
    void shouldReturnZeroRateForNoEvaluations() {
        assertEquals(0.0, summary.getTriggerRate(), 0.01);
    }

    @Test
    void shouldReturnNoneWhenNoTriggeredRules() {
        summary.recordEvaluation("r1", "CPU", false);
        assertEquals("NONE", summary.getMostTriggeredRule());
    }
}
