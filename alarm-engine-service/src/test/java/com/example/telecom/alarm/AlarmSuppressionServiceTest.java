package com.example.telecom.alarm;

import com.example.telecom.alarm.domain.AlarmSuppressionRule;
import com.example.telecom.alarm.repository.AlarmSuppressionRuleRepository;
import com.example.telecom.alarm.rule.SuppressionRuleEvaluator;
import com.example.telecom.alarm.service.AlarmSuppressionService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmSuppressionServiceTest {

    private AlarmSuppressionService suppressionService;
    private AlarmSuppressionRuleRepository ruleRepository;
    private SuppressionRuleEvaluator ruleEvaluator;

    @BeforeEach
    void setUp() {
        ruleRepository = new AlarmSuppressionRuleRepository();
        ruleEvaluator = new SuppressionRuleEvaluator();
        suppressionService = new AlarmSuppressionService(ruleRepository, ruleEvaluator);
    }

    @Test
    void shouldNotSuppressWhenNoMatchingRule() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());

        assertFalse(suppressionService.shouldSuppress(alarm));
    }

    @Test
    void shouldSuppressWhenRuleMatches() {
        AlarmSuppressionRule rule = new AlarmSuppressionRule(
                "r1", "Suppress CPU warnings", "dev-1", "CPU",
                Severity.WARNING, 60, true, System.currentTimeMillis());
        ruleRepository.save(rule);

        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.WARNING, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());

        assertTrue(suppressionService.shouldSuppress(alarm));
    }

    @Test
    void shouldNotSuppressWhenRuleIsDisabled() {
        AlarmSuppressionRule rule = new AlarmSuppressionRule(
                "r1", "Suppress CPU warnings", "dev-1", "CPU",
                Severity.CRITICAL, 60, false, System.currentTimeMillis());
        ruleRepository.save(rule);

        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());

        assertFalse(suppressionService.shouldSuppress(alarm));
    }

    @Test
    void shouldManageSuppressionRules() {
        AlarmSuppressionRule rule = new AlarmSuppressionRule(
                "r1", "Test Rule", "dev-*", "*",
                Severity.MAJOR, 30, true, System.currentTimeMillis());
        suppressionService.addSuppressionRule(rule);

        assertEquals(1, suppressionService.listSuppressionRules().size());

        suppressionService.removeSuppressionRule("r1");
        assertEquals(0, suppressionService.listSuppressionRules().size());
    }

    @Test
    void shouldTrackSuppressedAlarms() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());

        String result = suppressionService.suppress(alarm);
        assertTrue(result.contains("SUPPRESSED"));

        List<AlarmRecord> suppressed = suppressionService.getSuppressedAlarms();
        assertEquals(1, suppressed.size());
        assertEquals("a1", suppressed.get(0).getAlarmId());
    }
}
