package com.example.telecom.collector;

import com.example.telecom.collector.dto.CollectorScheduleRequest;
import com.example.telecom.collector.dto.CollectorScheduleResponse;
import com.example.telecom.collector.repository.CollectorScheduleRepository;
import com.example.telecom.collector.service.CollectorScheduleService;
import com.example.telecom.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectorScheduleServiceTest {

    private CollectorScheduleService scheduleService;
    private CollectorScheduleRepository scheduleRepository;

    @BeforeEach
    void setUp() {
        scheduleRepository = new CollectorScheduleRepository();
        scheduleService = new CollectorScheduleService(scheduleRepository);
    }

    @Test
    void shouldCreateSchedule() {
        CollectorScheduleRequest request = createRequest("Nightly-Metrics",
                List.of("DEV-001", "DEV-002"), List.of("CPU", "MEMORY"), 300, null);

        CollectorScheduleResponse response = scheduleService.createSchedule(request);

        assertNotNull(response.getScheduleId());
        assertEquals("Nightly-Metrics", response.getScheduleName());
        assertEquals(2, response.getDeviceCount());
        assertEquals(300, response.getIntervalSeconds());
        assertEquals("CREATED", response.getStatus());
    }

    @Test
    void shouldCreateAndStartEnabledSchedule() {
        CollectorScheduleRequest request = createRequest("RealTime-Check",
                List.of("DEV-003"), List.of("CPU"), 60, null);
        request.setEnabled(true);

        CollectorScheduleResponse response = scheduleService.createSchedule(request);

        assertEquals("RUNNING", response.getStatus());
        assertTrue(response.getNextRunTime() > 0);
    }

    @Test
    void shouldRejectEmptyDeviceIds() {
        CollectorScheduleRequest request = createRequest("Empty-Devices",
                List.of(), List.of("CPU"), 300, null);

        assertThrows(ValidationException.class, () -> scheduleService.createSchedule(request));
    }

    @Test
    void shouldStartAndStopSchedule() {
        CollectorScheduleRequest request = createRequest("Test-Schedule",
                List.of("DEV-001"), List.of("CPU", "MEMORY", "DISK"), 120, null);
        CollectorScheduleResponse created = scheduleService.createSchedule(request);

        CollectorScheduleResponse started = scheduleService.startSchedule(created.getScheduleId());
        assertEquals("RUNNING", started.getStatus());

        CollectorScheduleResponse stopped = scheduleService.stopSchedule(created.getScheduleId());
        assertEquals("PAUSED", stopped.getStatus());
    }

    @Test
    void shouldListSchedules() {
        scheduleService.createSchedule(createRequest("Sched-1", List.of("D1"), List.of("CPU"), 60, null));
        scheduleService.createSchedule(createRequest("Sched-2", List.of("D2"), List.of("MEMORY"), 120, null));
        scheduleService.createSchedule(createRequest("Sched-3", List.of("D3"), List.of("DISK"), 180, null));

        List<CollectorScheduleResponse> all = scheduleService.listSchedules();
        assertEquals(3, all.size());
    }

    @Test
    void shouldUpdateSchedule() {
        CollectorScheduleResponse created = scheduleService.createSchedule(
                createRequest("Original", List.of("DEV-001"), List.of("CPU"), 300, null));

        CollectorScheduleRequest updateReq = new CollectorScheduleRequest();
        updateReq.setScheduleName("Updated-Name");
        updateReq.setIntervalSeconds(600);

        CollectorScheduleResponse updated = scheduleService.updateSchedule(created.getScheduleId(), updateReq);
        assertEquals("Updated-Name", updated.getScheduleName());
        assertEquals(600, updated.getIntervalSeconds());
    }

    @Test
    void shouldDeleteSchedule() {
        CollectorScheduleResponse created = scheduleService.createSchedule(
                createRequest("To-Delete", List.of("DEV-001"), List.of("CPU"), 300, null));

        scheduleService.deleteSchedule(created.getScheduleId());
        assertThrows(ValidationException.class, () -> scheduleService.getSchedule(created.getScheduleId()));
    }

    @Test
    void shouldThrowForStoppingNonRunningSchedule() {
        CollectorScheduleResponse created = scheduleService.createSchedule(
                createRequest("Not-Running", List.of("DEV-001"), List.of("CPU"), 300, null));

        assertThrows(ValidationException.class, () -> scheduleService.stopSchedule(created.getScheduleId()));
    }

    @Test
    void shouldExecuteProcessingCycle() {
        CollectorScheduleRequest request = createRequest("Processing",
                List.of("DEV-001", "DEV-002"), List.of("CPU", "MEMORY"), 60, null);
        request.setEnabled(true);
        CollectorScheduleResponse created = scheduleService.createSchedule(request);

        CollectorScheduleResponse processed = scheduleService.process(created.getScheduleId());
        assertEquals("RUNNING", processed.getStatus());
        assertTrue(processed.getLastRunTime() > 0);
        assertTrue(processed.getNextRunTime() > processed.getLastRunTime());
    }

    private CollectorScheduleRequest createRequest(String name, List<String> deviceIds,
                                                    List<String> metricTypes, int interval, String cron) {
        CollectorScheduleRequest request = new CollectorScheduleRequest();
        request.setScheduleName(name);
        request.setDeviceIds(deviceIds);
        request.setMetricTypes(metricTypes);
        request.setIntervalSeconds(interval);
        request.setCronExpression(cron);
        return request;
    }
}
