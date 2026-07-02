package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.repository.MaintenanceWindowRepository;
import com.example.telecom.alarm.service.*;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmSuppressionServiceTest {

    private AlarmSuppressionService suppressionService;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        MaintenanceWindowRepository windowRepo = new MaintenanceWindowRepository();
        MaintenanceWindowService windowService = new MaintenanceWindowService(windowRepo);
        suppressionService = new AlarmSuppressionService(windowService);
        alarmRepository = new AlarmRepository();
    }

    @Test
    void shouldNotSuppressWhenNoMaintenanceWindow() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        assertFalse(suppressionService.shouldSuppress(alarm));
    }

    @Test
    void shouldFilterSuppressedAlarms() {
        AlarmRecord a1 = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        AlarmRecord a2 = new AlarmRecord("a2", "dev-2", "m2", "MEMORY_USAGE",
                Severity.WARNING, AlarmStatus.OPEN, "desc", "WEST", System.currentTimeMillis());

        List<AlarmRecord> filtered = suppressionService.filterSuppressed(List.of(a1, a2));
        assertFalse(filtered.isEmpty());
    }

    @Test
    void evaluateShouldReturnMessage() {
        AlarmRecord alarm = new AlarmRecord("a3", "dev-3", "m3", "TEMPERATURE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "SOUTH", System.currentTimeMillis());
        String result = suppressionService.evaluate(alarm);
        assertTrue(result.contains("PROCESS"));
    }
}
