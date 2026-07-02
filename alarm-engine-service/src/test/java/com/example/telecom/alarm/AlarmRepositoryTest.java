package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmRepositoryTest {

    private AlarmRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AlarmRepository();

        repository.save(new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "CPU critical", "EAST", 1000L));
        repository.save(new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.WARNING, AlarmStatus.ACKED, "Mem warning", "EAST", 2000L));
        repository.save(new AlarmRecord("a3", "dev-2", "m3", "OPTICAL_POWER",
                Severity.MAJOR, AlarmStatus.CLEARED, "Optical major", "WEST", 3000L));
    }

    @Test
    void shouldFindAlarmById() {
        assertTrue(repository.findById("a1").isPresent());
        assertEquals("EAST", repository.findById("a1").get().getAlarmRegionCode());
    }

    @Test
    void shouldFindByDeviceId() {
        List<AlarmRecord> device1Alarms = repository.findByDeviceId("dev-1");
        assertEquals(2, device1Alarms.size());

        List<AlarmRecord> device2Alarms = repository.findByDeviceId("dev-2");
        assertEquals(1, device2Alarms.size());
    }

    @Test
    void shouldFindActiveAlarms() {
        List<AlarmRecord> active = repository.findActiveAlarms();
        assertEquals(2, active.size()); // a1 OPEN + a2 ACKED
        assertTrue(active.stream().noneMatch(a -> a.getStatus() == AlarmStatus.CLEARED));
    }

    @Test
    void shouldFindAllAlarms() {
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void shouldReturnEmptyForMissingId() {
        assertTrue(repository.findById("nonexistent").isEmpty());
    }
}
