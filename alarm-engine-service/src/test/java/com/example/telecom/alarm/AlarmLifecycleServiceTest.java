package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmLifecycleService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmLifecycleServiceTest {

    private AlarmLifecycleService lifecycleService;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        lifecycleService = new AlarmLifecycleService(alarmRepository);
    }

    @Test
    void shouldAcknowledgeOpenAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        AlarmRecord result = lifecycleService.acknowledge("a1");
        assertEquals(AlarmStatus.ACKED, result.getStatus());
    }

    @Test
    void shouldClearAlarm() {
        AlarmRecord alarm = new AlarmRecord("a2", "d2", "m2", "MEMORY_USAGE",
                Severity.WARNING, AlarmStatus.ACKED, "Mem high", "WEST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        AlarmRecord result = lifecycleService.clear("a2");
        assertEquals(AlarmStatus.CLEARED, result.getStatus());
    }

    @Test
    void shouldThrowForNonExistentAlarm() {
        assertThrows(IllegalArgumentException.class, () -> lifecycleService.acknowledge("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> lifecycleService.clear("nonexistent"));
    }

    @Test
    void shouldUpdateTimestampOnAcknowledge() throws InterruptedException {
        AlarmRecord alarm = new AlarmRecord("a3", "d3", "m3", "TEMPERATURE",
                Severity.CRITICAL, AlarmStatus.OPEN, "Overheat", "EAST", System.currentTimeMillis());
        long originalTime = alarm.getUpdatedTime();
        alarmRepository.save(alarm);

        Thread.sleep(5);
        AlarmRecord result = lifecycleService.acknowledge("a3");
        assertTrue(result.getUpdatedTime() > originalTime);
    }
}
