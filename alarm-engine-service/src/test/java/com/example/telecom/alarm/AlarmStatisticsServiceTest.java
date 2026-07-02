package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmStatisticsService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlarmStatisticsServiceTest {

    private AlarmStatisticsService statisticsService;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        statisticsService = new AlarmStatisticsService(alarmRepository);

        // Add diverse test data
        alarmRepository.save(new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "CPU critical", "EAST", 1000L));
        alarmRepository.save(new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.MAJOR, AlarmStatus.ACKED, "Memory major", "EAST", 2000L));
        alarmRepository.save(new AlarmRecord("a3", "dev-2", "m3", "OPTICAL_POWER",
                Severity.CRITICAL, AlarmStatus.OPEN, "Optical critical", "WEST", 3000L));
        alarmRepository.save(new AlarmRecord("a4", "dev-2", "m4", "PACKET_LOSS",
                Severity.WARNING, AlarmStatus.CLEARED, "Loss warning", "WEST", 4000L));
        alarmRepository.save(new AlarmRecord("a5", "dev-3", "m5", "TEMPERATURE",
                Severity.MAJOR, AlarmStatus.OPEN, "Temp major", "SOUTH", 5000L));
    }

    @Test
    void shouldCountBySeverity() {
        Map<Severity, Long> counts = statisticsService.countBySeverity();
        assertEquals(2, counts.getOrDefault(Severity.CRITICAL, 0L));
        assertEquals(2, counts.getOrDefault(Severity.MAJOR, 0L));
        assertEquals(1, counts.getOrDefault(Severity.WARNING, 0L));
    }

    @Test
    void shouldCountByDevice() {
        Map<String, Long> counts = statisticsService.countByDevice();
        assertEquals(2, counts.getOrDefault("dev-1", 0L));
        assertEquals(2, counts.getOrDefault("dev-2", 0L));
        assertEquals(1, counts.getOrDefault("dev-3", 0L));
    }

    @Test
    void shouldCountByRegion() {
        Map<String, Long> counts = statisticsService.countByRegion();
        assertEquals(2, counts.getOrDefault("EAST", 0L));
        assertEquals(2, counts.getOrDefault("WEST", 0L));
        assertEquals(1, counts.getOrDefault("SOUTH", 0L));
    }

    @Test
    void shouldCountActiveAlarms() {
        assertEquals(4, statisticsService.countActiveAlarms());
    }

    @Test
    void shouldGenerateReport() {
        String report = statisticsService.generateReport();
        assertTrue(report.contains("total=5"));
        assertTrue(report.contains("active=4"));
        assertTrue(report.contains("critical=2"));
        assertTrue(report.contains("major=2"));
    }

    @Test
    void shouldFindTopCriticalAlarms() {
        var topAlarms = statisticsService.findTopCriticalAlarms(3);
        assertEquals(2, topAlarms.size());
    }

    @Test
    void shouldCalculateAverageAlarmsPerDevice() {
        double avg = statisticsService.averageAlarmsPerDevice();
        assertEquals(5.0 / 3.0, avg, 0.01);
    }
}
