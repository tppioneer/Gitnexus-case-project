package com.example.telecom.alarm;

import com.example.telecom.alarm.service.AlarmExportAsyncService;
import com.example.telecom.alarm.dto.AlarmExportRequest;
import com.example.telecom.alarm.dto.AlarmExportResponse;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AlarmExportAsyncServiceTest {

    private AlarmExportAsyncService exportService;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        exportService = new AlarmExportAsyncService(alarmRepository);

        alarmRepository.save(new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "EAST", System.currentTimeMillis()));
        alarmRepository.save(new AlarmRecord("a2", "dev-2", "m2", "MEMORY_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "WEST", System.currentTimeMillis()));
    }

    @Test
    void shouldStartExportJob() {
        AlarmExportRequest request = new AlarmExportRequest();
        request.setFormat("CSV");

        AlarmExportResponse response = exportService.exportAlarms(request);
        assertNotNull(response.getJobId());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void shouldReturnExportStatus() throws InterruptedException {
        AlarmExportRequest request = new AlarmExportRequest();
        request.setFormat("JSON");

        AlarmExportResponse initial = exportService.exportAlarms(request);
        Thread.sleep(200);

        Optional<AlarmExportResponse> status = exportService.getExportStatus(initial.getJobId());
        assertTrue(status.isPresent());
        assertTrue("COMPLETED".equals(status.get().getStatus())
                || "PROCESSING".equals(status.get().getStatus()));
    }

    @Test
    void shouldReturnEmptyForUnknownJob() {
        Optional<AlarmExportResponse> status = exportService.getExportStatus("unknown-job");
        assertFalse(status.isPresent());
    }

    @Test
    void shouldCancelPendingJob() {
        AlarmExportRequest request = new AlarmExportRequest();
        request.setFormat("CSV");

        AlarmExportResponse response = exportService.exportAlarms(request);
        boolean cancelled = exportService.cancelExport(response.getJobId());
        assertTrue(cancelled);
    }

    @Test
    void shouldNotCancelUnknownJob() {
        assertFalse(exportService.cancelExport("unknown-job"));
    }

    @Test
    void shouldListCompletedExports() throws InterruptedException {
        AlarmExportRequest request = new AlarmExportRequest();
        request.setFormat("CSV");
        exportService.exportAlarms(request);

        Thread.sleep(200);
        List<AlarmExportResponse> completed = exportService.getCompletedExports();
        assertFalse(completed.isEmpty());
    }
}
