package com.example.telecom.collector;

import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.collector.service.MetricAggregationService;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.DeviceMetricSummary;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetricAggregationServiceTest {

    private MetricAggregationService aggregationService;
    private DeviceMetricRepository metricRepository;

    @BeforeEach
    void setUp() {
        metricRepository = new DeviceMetricRepository();
        aggregationService = new MetricAggregationService(metricRepository);

        // Add multiple metrics for the same device
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            DeviceMetric metric = new DeviceMetric(
                    "m-cpu-" + i, "dev-1", MetricType.CPU_USAGE,
                    50.0 + i * 5, "%", Instant.ofEpochMilli(now + i * 60000), "Huawei");
            metricRepository.save(metric);
        }
        for (int i = 0; i < 5; i++) {
            DeviceMetric metric = new DeviceMetric(
                    "m-mem-" + i, "dev-1", MetricType.MEMORY_USAGE,
                    60.0 + i * 3, "%", Instant.ofEpochMilli(now + i * 60000), "Huawei");
            metricRepository.save(metric);
        }
        // Add metrics for another device
        metricRepository.save(new DeviceMetric("m-other", "dev-2", MetricType.CPU_USAGE,
                30.0, "%", Instant.ofEpochMilli(now), "ZTE"));
    }

    @Test
    void shouldSummarizeDeviceMetrics() {
        DeviceMetricSummary summary = aggregationService.summarizeDeviceMetrics("dev-1", "EAST");
        assertEquals("dev-1", summary.getDeviceId());
        assertEquals("EAST", summary.getDeviceRegionCode());
        assertEquals(15, summary.getSampleCount());

        Map<String, Double> latestValues = summary.getLatestValues();
        assertTrue(latestValues.containsKey("CPU_USAGE"));
        assertTrue(latestValues.containsKey("MEMORY_USAGE"));
    }

    @Test
    void shouldCalculateAverageValues() {
        DeviceMetricSummary summary = aggregationService.summarizeDeviceMetrics("dev-1", "EAST");
        Map<String, Double> averages = summary.getAverageValues();

        assertTrue(averages.containsKey("CPU_USAGE"));
        // Average of 50, 55, 60, ..., 95 = (50+95)*10/2/10 = 72.5
        assertEquals(72.5, averages.get("CPU_USAGE"), 0.1);
    }

    @Test
    void shouldCalculateMaxValues() {
        DeviceMetricSummary summary = aggregationService.summarizeDeviceMetrics("dev-1", "EAST");
        Map<String, Double> maxValues = summary.getMaxValues();

        assertEquals(95.0, maxValues.get("CPU_USAGE"), 0.01);
        assertEquals(72.0, maxValues.get("MEMORY_USAGE"), 0.01);
    }

    @Test
    void shouldCalculateMinValues() {
        DeviceMetricSummary summary = aggregationService.summarizeDeviceMetrics("dev-1", "EAST");
        Map<String, Double> minValues = summary.getMinValues();

        assertEquals(50.0, minValues.get("CPU_USAGE"), 0.01);
        assertEquals(60.0, minValues.get("MEMORY_USAGE"), 0.01);
    }

    @Test
    void shouldSummarizeAllDevices() {
        Map<String, DeviceMetricSummary> summaries = aggregationService.summarizeAllDevices();
        assertEquals(2, summaries.size());
        assertTrue(summaries.containsKey("dev-1"));
        assertTrue(summaries.containsKey("dev-2"));
    }

    @Test
    void shouldHandleEmptyMetrics() {
        DeviceMetricSummary summary = aggregationService.summarizeDeviceMetrics("dev-nonexistent", "EAST");
        assertEquals(0, summary.getSampleCount());
    }
}
