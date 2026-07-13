package com.example.telecom.alarm;

import com.example.telecom.alarm.domain.AlarmCorrelationRule;
import com.example.telecom.alarm.repository.AlarmCorrelationRuleRepository;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmCorrelationEngine;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlarmCorrelationEngineTest {

    private AlarmCorrelationEngine correlationEngine;
    private AlarmRepository alarmRepository;
    private AlarmCorrelationRuleRepository ruleRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        ruleRepository = new AlarmCorrelationRuleRepository();
        correlationEngine = new AlarmCorrelationEngine(alarmRepository, ruleRepository);

        AlarmCorrelationRule rule = new AlarmCorrelationRule(
                "cr1", "Same device correlation", "DEVICE",
                30, "dev-1", "*",
                Severity.WARNING, Severity.CRITICAL, true);
        ruleRepository.save(rule);
    }

    @Test
    void shouldCorrelateAlarmsOnSameDevice() {
        AlarmRecord existing1 = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis() - 10000);
        AlarmRecord existing2 = new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.WARNING, AlarmStatus.OPEN, "Mem high", "EAST", System.currentTimeMillis() - 5000);
        alarmRepository.save(existing1);
        alarmRepository.save(existing2);

        AlarmRecord newAlarm = new AlarmRecord("a3", "dev-1", "m3", "OPTICAL_POWER",
                Severity.CRITICAL, AlarmStatus.OPEN, "Optical low", "EAST", System.currentTimeMillis());

        List<AlarmRecord> correlated = correlationEngine.correlate(newAlarm);
        assertEquals(2, correlated.size());
    }

    @Test
    void shouldNotCorrelateAlarmsOnDifferentDevices() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(existing);

        AlarmRecord newAlarm = new AlarmRecord("a2", "dev-other", "m2", "TEMP",
                Severity.CRITICAL, AlarmStatus.OPEN, "Overheat", "WEST", System.currentTimeMillis());

        List<AlarmRecord> correlated = correlationEngine.correlate(newAlarm);
        assertTrue(correlated.isEmpty());
    }

    @Test
    void shouldBuildCorrelationGroup() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis() - 10000);
        alarmRepository.save(existing);

        AlarmRecord newAlarm = new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "Mem critical", "EAST", System.currentTimeMillis());

        List<AlarmRecord> correlated = correlationEngine.correlate(newAlarm);
        assertFalse(correlated.isEmpty());

        Map<String, List<String>> groups = correlationEngine.getCorrelationGroups();
        assertFalse(groups.isEmpty());

        boolean found = groups.values().stream()
                .anyMatch(ids -> ids.contains("a2") && ids.contains("a1"));
        assertTrue(found);
    }

    @Test
    void shouldMergeCorrelatedAlarms() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis() - 10000);
        alarmRepository.save(existing);

        AlarmRecord newAlarm = new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "Mem critical", "EAST", System.currentTimeMillis());

        correlationEngine.correlate(newAlarm);

        AlarmRecord savedNew = alarmRepository.findById("a2").orElseThrow();
        assertTrue(savedNew.getDescription().contains("correlated"));
    }
}
