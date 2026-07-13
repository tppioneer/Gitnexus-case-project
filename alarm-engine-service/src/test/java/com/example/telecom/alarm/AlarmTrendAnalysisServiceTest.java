package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmTrendAnalysisService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlarmTrendAnalysisServiceTest {

    private AlarmTrendAnalysisService trendAnalysisService;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        trendAnalysisService = new AlarmTrendAnalysisService(alarmRepository);

        long now = System.currentTimeMillis();
        alarmRepository.save(new AlarmRecord("a1", "dev-1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "CPU critical", "EAST", now - 3600000));
        alarmRepository.save(new AlarmRecord("a2", "dev-1", "m2", "MEMORY_USAGE",
                Severity.MAJOR, AlarmStatus.ACKED, "Memory major", "EAST", now - 7200000));
        alarmRepository.save(new AlarmRecord("a3", "dev-2", "m3", "OPTICAL_POWER",
                Severity.CRITICAL, AlarmStatus.OPEN, "Optical critical", "WEST", now - 1800000));
        alarmRepository.save(new AlarmRecord("a4", "dev-2", "m4", "PACKET_LOSS",
                Severity.WARNING, AlarmStatus.CLEARED, "Loss warning", "WEST", now - 3600000));
        alarmRepository.save(new AlarmRecord("a5", "dev-3", "m5", "TEMPERATURE",
                Severity.MAJOR, AlarmStatus.OPEN, "Temp major", "SOUTH", now - 600000));
    }

    @Test
    void shouldAnalyzeTimeRange() {
        Map<String, Object> analysis = trendAnalysisService.analyze("24h");
        assertNotNull(analysis);
        assertEquals("24h", analysis.get("timeRange"));
        assertEquals(5, analysis.get("totalAlarms"));
    }

    @Test
    void shouldGetSeverityTrend() {
        Map<Severity, Long> trend = trendAnalysisService.getSeverityTrend();
        assertEquals(2, trend.getOrDefault(Severity.CRITICAL, 0L));
        assertEquals(2, trend.getOrDefault(Severity.MAJOR, 0L));
        assertEquals(1, trend.getOrDefault(Severity.WARNING, 0L));
    }

    @Test
    void shouldGetTopAlarmTypes() {
        List<Map.Entry<String, Long>> topTypes = trendAnalysisService.getTopAlarmTypes(3);
        assertEquals(3, topTypes.size());
    }
}
