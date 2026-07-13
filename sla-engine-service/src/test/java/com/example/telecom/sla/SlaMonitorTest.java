package com.example.telecom.sla;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.calculator.*;
import com.example.telecom.sla.monitor.SlaBreachPolicy;
import com.example.telecom.sla.monitor.SlaMonitor;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.repository.SlaMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlaMonitorTest {

    @Mock
    private SlaContractRepository contractRepository;
    @Mock
    private SlaMetricRepository metricRepository;

    private SlaBreachPolicy breachPolicy;
    private SlaCalculatorRegistry registry;
    private SlaMonitor slaMonitor;

    private SlaContract contract;
    private List<SlaMetricSnapshot> metrics;

    @BeforeEach
    void setUp() {
        breachPolicy = new SlaBreachPolicy();
        registry = new SlaCalculatorRegistry();
        registry.register("time", new SlaTimeCalculator());
        registry.register("availability", new SlaAvailabilityCalculator());

        slaMonitor = new SlaMonitor(contractRepository, metricRepository, breachPolicy, registry);

        contract = SlaContract.builder()
                .contractId("contract-001")
                .contractName("Test Contract")
                .vendorId("vendor-001")
                .regionCode("US-EAST")
                .responseTimeThreshold(120)
                .resolutionTimeThreshold(360)
                .availabilityTarget(99.5)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .status(VendorSlaStatus.MET)
                .createdTime(LocalDateTime.now())
                .build();

        metrics = List.of(
                new SlaMetricSnapshot("snap-001", "contract-001", "response_time",
                        95.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET),
                new SlaMetricSnapshot("snap-002", "contract-001", "availability",
                        99.8, 99.5, LocalDateTime.now(), VendorSlaStatus.MET)
        );
    }

    @Test
    void monitor_shouldReturnResult_whenContractExists() {
        when(contractRepository.findById("contract-001")).thenReturn(contract);
        when(metricRepository.findByContractId("contract-001")).thenReturn(metrics);

        SlaMonitor.SlaMonitorResult result = slaMonitor.monitor("contract-001");

        assertNotNull(result);
        assertEquals("contract-001", result.getContractId());
        assertEquals("Test Contract", result.getContractName());
        assertEquals(2, result.getTotalMetrics());
        assertEquals(0, result.getBreachCount());
        assertTrue(result.getBreachedMetrics().isEmpty());
        assertTrue(result.getOverallCompliance() >= 0);
    }

    @Test
    void monitor_shouldDetectBreaches_whenMetricsExceedThresholds() {
        List<SlaMetricSnapshot> badMetrics = List.of(
                new SlaMetricSnapshot("snap-003", "contract-001", "response_time",
                        200.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET),
                new SlaMetricSnapshot("snap-004", "contract-001", "availability",
                        95.0, 99.5, LocalDateTime.now(), VendorSlaStatus.MET)
        );

        when(contractRepository.findById("contract-001")).thenReturn(contract);
        when(metricRepository.findByContractId("contract-001")).thenReturn(badMetrics);

        SlaMonitor.SlaMonitorResult result = slaMonitor.monitor("contract-001");

        assertNotNull(result);
        assertEquals(2, result.getBreachCount());
        assertTrue(result.getBreachedMetrics().contains("response_time"));
        assertTrue(result.getBreachedMetrics().contains("availability"));
        assertEquals(2, result.getBreachDetails().size());
    }

    @Test
    void collectMetrics_shouldReturnAllMetricsForContract() {
        when(metricRepository.findByContractId("contract-001")).thenReturn(metrics);

        List<SlaMetricSnapshot> collected = slaMonitor.collectMetrics("contract-001");

        assertNotNull(collected);
        assertEquals(2, collected.size());
    }

    @Test
    void triggerBreachCheck_shouldDetectBreaches() {
        List<SlaMetricSnapshot> badMetrics = List.of(
                new SlaMetricSnapshot("snap-005", "contract-001", "response_time",
                        250.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET)
        );

        when(contractRepository.findById("contract-001")).thenReturn(contract);
        when(metricRepository.findByContractId("contract-001")).thenReturn(badMetrics);

        List<String> breached = slaMonitor.triggerBreachCheck("contract-001");

        assertNotNull(breached);
        assertEquals(1, breached.size());
        assertEquals("response_time", breached.get(0));
    }
}
