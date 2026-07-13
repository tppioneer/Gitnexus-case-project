package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmSummaryReportGenerator;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmSummaryReportGeneratorTest {

    private AlarmSummaryReportGenerator reportGenerator;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        reportGenerator = new AlarmSummaryReportGenerator(alarmRepository);

        long now = System.currentTimeMillis();
        alarmRepository.save(new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "CPU critical", "EAST", now - 3600000));
        alarmRepository.save(new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.MAJOR, AlarmStatus.ACKED, "Memory major", "EAST", now - 7200000));
        alarmRepository.save(new AlarmRecord("a3", "dev-2", "m3", "OPTICAL_POWER",
                Severity.CRITICAL, AlarmStatus.OPEN, "Optical critical", "WEST", now - 1800000));
    }

    @Test
    void shouldGenerateReportForPeriod() {
        String report = reportGenerator.generate("24h");
        assertNotNull(report);
        assertTrue(report.contains("Total Alarms"));
        assertTrue(report.contains("Critical: 2"));
        assertTrue(report.contains("Major: 1"));
    }

    @Test
    void shouldGenerateBySeverityReport() {
        String report = reportGenerator.generateBySeverity();
        assertTrue(report.contains("CRITICAL"));
        assertTrue(report.contains("MAJOR"));
    }

    @Test
    void shouldGenerateByRegionReport() {
        String report = reportGenerator.generateByRegion();
        assertTrue(report.contains("EAST"));
        assertTrue(report.contains("WEST"));
    }

    @Test
    void shouldGenerateTrendReport() {
        String report = reportGenerator.generateTrendReport();
        assertTrue(report.contains("Last 24h"));
        assertTrue(report.contains("Last 7 days"));
    }
}
