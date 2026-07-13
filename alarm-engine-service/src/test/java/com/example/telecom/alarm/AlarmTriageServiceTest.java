package com.example.telecom.alarm;

import com.example.telecom.alarm.domain.AlarmTriageRule;
import com.example.telecom.alarm.repository.AlarmTriageRuleRepository;
import com.example.telecom.alarm.service.AlarmTriageService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmTriageServiceTest {

    private AlarmTriageService triageService;
    private AlarmTriageRuleRepository ruleRepository;

    @BeforeEach
    void setUp() {
        ruleRepository = new AlarmTriageRuleRepository();
        triageService = new AlarmTriageService(ruleRepository);

        ruleRepository.save(new AlarmTriageRule("tr1", "Critical CPU", "severity=CRITICAL",
                1, "NETWORK_CRITICAL", "Escalate immediately", true));
        ruleRepository.save(new AlarmTriageRule("tr2", "Major Memory", "severity=MAJOR",
                2, "RESOURCE_ISSUE", "Schedule investigation", true));
        ruleRepository.save(new AlarmTriageRule("tr3", "Warning Temp", "severity=WARNING",
                3, "ENVIRONMENTAL", "Monitor", true));
    }

    @Test
    void shouldClassifyCriticalAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "CPU overload", "EAST", System.currentTimeMillis());

        String category = triageService.classify(alarm);
        assertEquals("NETWORK_CRITICAL", category);
    }

    @Test
    void shouldTriageAlarmAndReturnResult() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "CPU overload", "EAST", System.currentTimeMillis());

        AlarmTriageService.TriageResult result = triageService.triage(alarm);

        assertNotNull(result);
        assertEquals("a1", result.getAlarmId());
        assertEquals("NETWORK_CRITICAL", result.getCategory());
        assertEquals(1, result.getPriority());
        assertNotNull(result.getSuggestedAction());
    }

    @Test
    void shouldSuggestActionBasedOnSeverity() {
        AlarmRecord critical = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        AlarmRecord cleared = new AlarmRecord("a2", "dev-2", "m2", "MEMORY_USAGE",
                Severity.MAJOR, AlarmStatus.CLEARED, "desc", "WEST", System.currentTimeMillis());
        AlarmRecord warning = new AlarmRecord("a3", "dev-3", "m3", "TEMP",
                Severity.WARNING, AlarmStatus.OPEN, "desc", "SOUTH", System.currentTimeMillis());

        String criticalAction = triageService.suggestAction(critical);
        assertTrue(criticalAction.contains("Immediate escalation"));

        String clearedAction = triageService.suggestAction(cleared);
        assertTrue(clearedAction.contains("already cleared"));

        String warningAction = triageService.suggestAction(warning);
        assertTrue(warningAction.contains("Monitor"));
    }
}
