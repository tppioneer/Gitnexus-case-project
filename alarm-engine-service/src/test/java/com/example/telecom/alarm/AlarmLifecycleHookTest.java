package com.example.telecom.alarm;

import com.example.telecom.alarm.policy.AlarmClearedHook;
import com.example.telecom.alarm.policy.AlarmCreationHook;
import com.example.telecom.alarm.policy.AlarmEscalationHook;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmCreationHookTest {

    private AlarmCreationHook creationHook;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        creationHook = new AlarmCreationHook(alarmRepository);
    }

    @Test
    void shouldEnrichAlarmOnCreation() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.WARNING, AlarmStatus.OPEN, "", "EAST", System.currentTimeMillis());

        creationHook.process(alarm);

        assertNotNull(alarm.getDescription());
        assertFalse(alarm.getDescription().isEmpty());
        assertEquals(AlarmStatus.OPEN, alarm.getStatus());
    }

    @Test
    void shouldSetStatusToOpen() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.INFO, AlarmStatus.CLEARED, "desc", "EAST", System.currentTimeMillis());

        creationHook.process(alarm);
        assertEquals(AlarmStatus.OPEN, alarm.getStatus());
    }
}

class AlarmClearedHookTest {

    private AlarmClearedHook clearedHook;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        clearedHook = new AlarmClearedHook(alarmRepository);
    }

    @Test
    void shouldSetStatusToCleared() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        clearedHook.process(alarm);
        assertEquals(AlarmStatus.CLEARED, alarm.getStatus());
    }

    @Test
    void shouldNotChangeAlreadyCleared() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.CLEARED, "desc", "EAST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        clearedHook.process(alarm);
        assertEquals(AlarmStatus.CLEARED, alarm.getStatus());
    }
}

class AlarmEscalationHookTest {

    private AlarmEscalationHook escalationHook;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        escalationHook = new AlarmEscalationHook(alarmRepository);
    }

    @Test
    void shouldNotEscalateNewAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        escalationHook.process(alarm);
        assertFalse(alarm.getDescription().contains("ESCALATED"));
    }

    @Test
    void shouldNotEscalateClearedAlarm() {
        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.CLEARED, "desc", "EAST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        escalationHook.process(alarm);
        assertEquals("desc", alarm.getDescription());
    }
}
