package com.example.telecom.alarm.federated.aggregation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AggregationStrategyTest {

    private List<FederatedAlarmRecord> testAlarms;

    @BeforeEach
    void setUp() {
        testAlarms = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        testAlarms.add(new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(30), "Power failure", "US-EAST", null));
        testAlarms.add(new FederatedAlarmRecord("src-2", "dev-2", "TEMP_HIGH",
                Severity.MAJOR, now.minusMinutes(20), "High temperature", "US-WEST", null));
        testAlarms.add(new FederatedAlarmRecord("src-1", "dev-3", "DISK_FULL",
                Severity.WARNING, now.minusMinutes(10), "Disk full", "US-EAST", null));
        testAlarms.add(new FederatedAlarmRecord("src-3", "dev-4", "NETWORK_DOWN",
                Severity.CRITICAL, now.minusMinutes(5), "Network down", "EU-WEST", null));
        testAlarms.add(new FederatedAlarmRecord("src-4", "dev-5", "POWER_FAILURE",
                Severity.WARNING, now, "Power warning", "APAC-EAST", null));
    }

    @Test
    void testRegionBasedAggregation() {
        RegionBasedAggregationStrategy strategy = new RegionBasedAggregationStrategy();
        AggregationResult result = strategy.aggregate(testAlarms);
        assertEquals("REGION_BASED", result.getStrategy());
        Map<String, Object> data = result.getGroupedData();
        assertNotNull(data);
        Map<String, Long> regionCounts = (Map<String, Long>) data.get("regionCounts");
        assertNotNull(regionCounts);
        assertEquals(4, regionCounts.size());
    }

    @Test
    void testSeverityBasedAggregation() {
        SeverityBasedAggregationStrategy strategy = new SeverityBasedAggregationStrategy();
        AggregationResult result = strategy.aggregate(testAlarms);
        assertEquals("SEVERITY_BASED", result.getStrategy());
        Map<String, Object> data = result.getGroupedData();
        assertNotNull(data);
        Map<String, Long> severityCounts = (Map<String, Long>) data.get("severityCounts");
        assertNotNull(severityCounts);
        assertEquals(3, severityCounts.size());
    }

    @Test
    void testTimeWindowAggregation() {
        TimeWindowAggregationStrategy strategy = new TimeWindowAggregationStrategy();
        AggregationResult result = strategy.aggregate(testAlarms);
        assertEquals("TIME_WINDOW", result.getStrategy());
        assertNotNull(result.getGroupedData());
    }
}
