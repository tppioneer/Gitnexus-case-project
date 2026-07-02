package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.MaintenanceWindowRepository;
import com.example.telecom.alarm.service.*;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SuppressionRuleEvaluatorTest {

    private SuppressionRuleEvaluator evaluator;

    @BeforeEach
    void setUp() {
        MaintenanceWindowRepository windowRepo = new MaintenanceWindowRepository();
        MaintenanceWindowService windowService = new MaintenanceWindowService(windowRepo);
        AlarmSuppressionService suppressionService = new AlarmSuppressionService(windowService);
        evaluator = new SuppressionRuleEvaluator(suppressionService);
    }

    @Test
    void shouldEvaluateSingleAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        assertFalse(evaluator.evaluate(alarm));
    }

    @Test
    void shouldEvaluateAlarmList() {
        List<AlarmRecord> alarms = List.of(
                new AlarmRecord("a1", "d1", "m1", "CPU_USAGE", Severity.CRITICAL, AlarmStatus.OPEN,
                        "d", "E", System.currentTimeMillis()),
                new AlarmRecord("a2", "d2", "m2", "MEMORY_USAGE", Severity.WARNING, AlarmStatus.OPEN,
                        "d", "W", System.currentTimeMillis())
        );
        String result = evaluator.evaluate(alarms);
        assertTrue(result.contains("0/2"));
    }
}
