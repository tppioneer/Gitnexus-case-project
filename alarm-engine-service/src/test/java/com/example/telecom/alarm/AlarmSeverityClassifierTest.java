package com.example.telecom.alarm;

import com.example.telecom.alarm.service.AlarmSeverityClassifier;
import com.example.telecom.common.alarm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmSeverityClassifierTest {

    private AlarmSeverityClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new AlarmSeverityClassifier();
    }

    @Test
    void shouldReturnInfoWhenNotTriggered() {
        EvaluationResult result = new EvaluationResult(false, "r1", "m1", 50.0, 80.0, null, "normal");
        ThresholdRule rule = new ThresholdRule("r1", "CPU", "CPU_USAGE", 80.0, Severity.MAJOR, true);

        assertEquals(Severity.INFO, classifier.classify(result, rule));
    }

    @Test
    void shouldReturnRuleSeverityWhenTriggered() {
        EvaluationResult result = new EvaluationResult(true, "r1", "m1", 90.0, 80.0, Severity.MAJOR, "high");
        ThresholdRule rule = new ThresholdRule("r1", "CPU", "CPU_USAGE", 80.0, Severity.MAJOR, true);

        assertEquals(Severity.MAJOR, classifier.classify(result, rule));
    }

    @Test
    void shouldEscalateWhenValueFarExceedsThreshold() {
        // ratio = 90/30 = 3.0, WARNING → MAJOR escalation
        EvaluationResult result = new EvaluationResult(true, "r2", "m2", 90.0, 30.0, Severity.WARNING, "high");
        ThresholdRule rule = new ThresholdRule("r2", "Mem", "MEMORY_USAGE", 30.0, Severity.WARNING, true);

        assertEquals(Severity.MAJOR, classifier.classify(result, rule));
    }

    @Test
    void shouldEscalateMajorToCriticalWhenRatioHigh() {
        // ratio = 90/20 = 4.5, MAJOR → CRITICAL escalation
        EvaluationResult result = new EvaluationResult(true, "r3", "m3", 90.0, 20.0, Severity.MAJOR, "very high");
        ThresholdRule rule = new ThresholdRule("r3", "Loss", "PACKET_LOSS", 20.0, Severity.MAJOR, true);

        assertEquals(Severity.CRITICAL, classifier.classify(result, rule));
    }

    @Test
    void shouldDefaultToWarningWhenRuleSeverityNull() {
        EvaluationResult result = new EvaluationResult(true, "r4", "m4", 85.0, 80.0, null, "triggered");
        ThresholdRule rule = new ThresholdRule("r4", "CPU", "CPU_USAGE", 80.0, null, true);

        assertEquals(Severity.WARNING, classifier.classify(result, rule));
    }

    @Test
    void shouldNotEscalateWhenRatioIsExactlyTwo() {
        EvaluationResult result = new EvaluationResult(true, "r5", "m5", 60.0, 30.0, Severity.WARNING, "boundary");
        ThresholdRule rule = new ThresholdRule("r5", "Test", "CPU_USAGE", 30.0, Severity.WARNING, true);

        // ratio = 2.0 → not > 2.0, so no escalation
        assertEquals(Severity.WARNING, classifier.classify(result, rule));
    }
}
