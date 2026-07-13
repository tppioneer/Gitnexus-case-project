package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmReportScheduler;
import com.example.telecom.alarm.service.AlarmSummaryReportGenerator;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlarmReportSchedulerTest {

    private AlarmReportScheduler scheduler;
    private AlarmSummaryReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        AlarmRepository alarmRepository = new AlarmRepository();
        alarmRepository.save(new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis()));
        reportGenerator = new AlarmSummaryReportGenerator(alarmRepository);
        scheduler = new AlarmReportScheduler(reportGenerator);
    }

    @Test
    void shouldScheduleReport() {
        AlarmReportScheduler.ScheduledReport report = scheduler.scheduleReport("daily-summary", "24h", 1440);
        assertNotNull(report);
        assertEquals("daily-summary", report.getReportId());
        assertEquals("24h", report.getPeriod());
        assertEquals("ACTIVE", report.getStatus());
    }

    @Test
    void shouldCancelSchedule() {
        scheduler.scheduleReport("test-report", "24h", 60);
        boolean cancelled = scheduler.cancelSchedule("test-report");
        assertTrue(cancelled);
    }

    @Test
    void shouldNotCancelUnknownSchedule() {
        assertFalse(scheduler.cancelSchedule("unknown"));
    }

    @Test
    void shouldListSchedules() {
        scheduler.scheduleReport("r1", "24h", 60);
        scheduler.scheduleReport("r2", "7d", 1440);

        List<AlarmReportScheduler.ScheduledReport> schedules = scheduler.listSchedules();
        assertEquals(2, schedules.size());
    }

    @Test
    void shouldExecuteDueReports() {
        scheduler.scheduleReport("due-report", "24h", 1);
        List<String> results = scheduler.executeDueReports();
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).contains("Total Alarms"));
    }
}
