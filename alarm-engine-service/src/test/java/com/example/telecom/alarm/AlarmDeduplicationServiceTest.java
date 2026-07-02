package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmDeduplicationService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmDeduplicationServiceTest {

    private AlarmRepository alarmRepository;
    private AlarmDeduplicationService dedupService;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        dedupService = new AlarmDeduplicationService(alarmRepository);
    }

    @Test
    void shouldDetectDuplicate() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(existing);

        AlarmRecord duplicate = new AlarmRecord("a2", "dev-1", "m2", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high again", "EAST", System.currentTimeMillis());

        assertTrue(dedupService.isDuplicate(duplicate));
    }

    @Test
    void shouldNotDetectDuplicateForClearedAlarm() {
        AlarmRecord existing = new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.CLEARED, "CPU high", "EAST", System.currentTimeMillis());
        alarmRepository.save(existing);

        AlarmRecord newAlarm = new AlarmRecord("a2", "dev-1", "m2", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "CPU high again", "EAST", System.currentTimeMillis());

        assertFalse(dedupService.isDuplicate(newAlarm));
    }
}
