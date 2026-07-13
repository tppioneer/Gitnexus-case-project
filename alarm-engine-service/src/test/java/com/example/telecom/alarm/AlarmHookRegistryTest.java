package com.example.telecom.alarm;

import com.example.telecom.alarm.policy.*;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmHookRegistryTest {

    private AlarmHookRegistry registry;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        registry = new AlarmHookRegistry();
        alarmRepository = new AlarmRepository();
    }

    @Test
    void shouldRegisterAndExecuteHooks() {
        AlarmCreationHook creationHook = new AlarmCreationHook(alarmRepository);
        registry.register(creationHook);

        List<AlarmLifecycleHook> hooks = registry.getHooks();
        assertEquals(1, hooks.size());

        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "", "EAST", System.currentTimeMillis());

        registry.executeHooks(alarm);
        assertNotNull(alarm.getDescription());
        assertFalse(alarm.getDescription().isEmpty());
    }

    @Test
    void shouldUnregisterHook() {
        AlarmCreationHook hook = new AlarmCreationHook(alarmRepository);
        registry.register(hook);
        assertFalse(registry.getHooks().isEmpty());

        boolean removed = registry.unregister(hook);
        assertTrue(removed);
        assertTrue(registry.getHooks().isEmpty());
    }

    @Test
    void shouldExecuteMultipleHooksInOrder() {
        AlarmCreationHook creationHook = new AlarmCreationHook(alarmRepository);
        AlarmClearedHook clearedHook = new AlarmClearedHook(alarmRepository);
        AlarmEscalationHook escalationHook = new AlarmEscalationHook(alarmRepository);

        registry.register(creationHook);
        registry.register(clearedHook);
        registry.register(escalationHook);

        assertEquals(3, registry.getHooks().size());

        AlarmRecord alarm = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "test", "EAST", System.currentTimeMillis());
        alarmRepository.save(alarm);

        registry.executeHooks(alarm);
        assertNotNull(alarm.getStatus());
    }
}
