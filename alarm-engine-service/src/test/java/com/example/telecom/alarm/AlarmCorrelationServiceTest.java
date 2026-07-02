package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmCorrelationService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmCorrelationServiceTest {

    private AlarmCorrelationService correlationService;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        correlationService = new AlarmCorrelationService(alarmRepository);
    }

    @Test
    void shouldCorrelateWithExistingDeviceAlarms() {
        AlarmRecord existing1 = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis());
        AlarmRecord existing2 = new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.WARNING, AlarmStatus.OPEN, "Mem high", "EAST", System.currentTimeMillis());
        alarmRepository.save(existing1);
        alarmRepository.save(existing2);

        AlarmRecord newAlarm = new AlarmRecord("a3", "dev-1", "m3", "OPTICAL_POWER",
                Severity.CRITICAL, AlarmStatus.OPEN, "Optical low", "EAST", System.currentTimeMillis());

        List<AlarmRecord> correlated = correlationService.correlate(newAlarm);
        assertEquals(2, correlated.size());
    }

    @Test
    void shouldNotCorrelateWithSelf() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(existing);

        List<AlarmRecord> correlated = correlationService.correlate(existing);
        assertTrue(correlated.isEmpty());
    }

    @Test
    void shouldExcludeClearedAlarmsFromCorrelation() {
        AlarmRecord cleared = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.CLEARED, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(cleared);

        AlarmRecord newAlarm = new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.WARNING, AlarmStatus.OPEN, "Mem high", "EAST", System.currentTimeMillis());

        List<AlarmRecord> correlated = correlationService.correlate(newAlarm);
        assertTrue(correlated.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForUnrelatedDevice() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(existing);

        AlarmRecord newAlarm = new AlarmRecord("a2", "dev-other", "m2", "TEMP",
                Severity.CRITICAL, AlarmStatus.OPEN, "Overheat", "WEST", System.currentTimeMillis());

        List<AlarmRecord> correlated = correlationService.correlate(newAlarm);
        assertTrue(correlated.isEmpty());
    }
}
