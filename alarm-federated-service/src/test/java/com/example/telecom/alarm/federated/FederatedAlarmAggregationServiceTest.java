package com.example.telecom.alarm.federated;

import com.example.telecom.alarm.federated.aggregation.AggregationResult;
import com.example.telecom.alarm.federated.aggregation.RegionBasedAggregationStrategy;
import com.example.telecom.alarm.federated.aggregation.SeverityBasedAggregationStrategy;
import com.example.telecom.alarm.federated.aggregation.TimeWindowAggregationStrategy;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.service.FederatedAlarmAggregationService;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FederatedAlarmAggregationServiceTest {

    @Mock
    private FederatedAlarmRepository alarmRepository;

    private FederatedAlarmAggregationService aggregationService;
    private List<FederatedAlarmRecord> testAlarms;

    @BeforeEach
    void setUp() {
        RegionBasedAggregationStrategy regionStrategy = new RegionBasedAggregationStrategy();
        SeverityBasedAggregationStrategy severityStrategy = new SeverityBasedAggregationStrategy();
        TimeWindowAggregationStrategy timeWindowStrategy = new TimeWindowAggregationStrategy();

        aggregationService = new FederatedAlarmAggregationService(
                alarmRepository, regionStrategy, severityStrategy, timeWindowStrategy);

        testAlarms = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        FederatedAlarmRecord alarm1 = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(10), "Power failure in rack A", "US-EAST", null);
        alarm1.setAlarmId("alarm-1");
        FederatedAlarmRecord alarm2 = new FederatedAlarmRecord("src-2", "dev-2", "TEMP_HIGH",
                Severity.MAJOR, now.minusMinutes(5), "High temperature in rack B", "US-WEST", null);
        alarm2.setAlarmId("alarm-2");
        FederatedAlarmRecord alarm3 = new FederatedAlarmRecord("src-1", "dev-3", "DISK_FULL",
                Severity.WARNING, now, "Disk usage above 90%", "US-EAST", null);
        alarm3.setAlarmId("alarm-3");
        FederatedAlarmRecord alarm4 = new FederatedAlarmRecord("src-3", "dev-4", "NETWORK_DOWN",
                Severity.CRITICAL, now.minusMinutes(30), "Network outage", "EU-WEST", null);
        alarm4.setAlarmId("alarm-4");
        FederatedAlarmRecord alarm5 = new FederatedAlarmRecord("src-2", "dev-5", "POWER_FAILURE",
                Severity.WARNING, now.minusHours(1), "Power fluctuation", "US-WEST", null);
        alarm5.setAlarmId("alarm-5");

        testAlarms.add(alarm1);
        testAlarms.add(alarm2);
        testAlarms.add(alarm3);
        testAlarms.add(alarm4);
        testAlarms.add(alarm5);
    }

    @Test
    void testAggregateByRegion() {
        AggregationResult result = aggregationService.aggregateByRegion(testAlarms);
        assertEquals("REGION_BASED", result.getStrategy());
        assertEquals(5, result.getTotalCount());
        Map<String, Object> data = result.getGroupedData();
        assertNotNull(data);
        Map<String, Long> regionCounts = (Map<String, Long>) data.get("regionCounts");
        assertEquals(3, regionCounts.size());
        assertEquals(2, regionCounts.get("US-EAST").longValue());
        assertEquals(2, regionCounts.get("US-WEST").longValue());
        assertEquals(1, regionCounts.get("EU-WEST").longValue());
    }

    @Test
    void testAggregateBySeverity() {
        AggregationResult result = aggregationService.aggregateBySeverity(testAlarms);
        assertEquals("SEVERITY_BASED", result.getStrategy());
        assertEquals(5, result.getTotalCount());
        Map<String, Object> data = result.getGroupedData();
        assertNotNull(data);
        Map<String, Long> severityCounts = (Map<String, Long>) data.get("severityCounts");
        assertEquals(3, severityCounts.size());
        assertEquals(2, severityCounts.get("CRITICAL").longValue());
        assertEquals(1, severityCounts.get("MAJOR").longValue());
        assertEquals(2, severityCounts.get("WARNING").longValue());
    }

    @Test
    void testAggregateByTimeWindow() {
        AggregationResult result = aggregationService.aggregateByTimeWindow(testAlarms, Duration.ofMinutes(30));
        assertEquals("TIME_WINDOW", result.getStrategy());
        assertEquals(5, result.getTotalCount());
        Map<String, Object> data = result.getGroupedData();
        assertNotNull(data);
        assertNotNull(data.get("windows"));
        assertNotNull(data.get("trend"));
    }

    @Test
    void testGetAggregationSummary() {
        when(alarmRepository.findAll()).thenReturn(testAlarms);
        FederatedAlarmSummary summary = aggregationService.getAggregationSummary();
        assertEquals(5, summary.getTotalCount());
        assertNotNull(summary.getBySeverity());
        assertNotNull(summary.getByRegion());
        assertNotNull(summary.getLastUpdated());
    }
}
