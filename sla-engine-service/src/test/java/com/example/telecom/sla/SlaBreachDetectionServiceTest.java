package com.example.telecom.sla;

import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.event.SlaEventPublisher;
import com.example.telecom.sla.monitor.SlaBreachPolicy;
import com.example.telecom.sla.repository.SlaBreachRepository;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.repository.SlaMetricRepository;
import com.example.telecom.sla.service.SlaBreachDetectionService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlaBreachDetectionServiceTest {

    @Mock
    private SlaContractRepository contractRepository;
    @Mock
    private SlaMetricRepository metricRepository;
    @Mock
    private SlaBreachRepository breachRepository;
    @Mock
    private SlaEventPublisher eventPublisher;

    private SlaBreachPolicy breachPolicy;
    private SlaBreachDetectionService breachDetectionService;

    private SlaContract contract;
    private SlaMetricSnapshot metricSnapshot;

    @BeforeEach
    void setUp() {
        breachPolicy = new SlaBreachPolicy();
        breachDetectionService = new SlaBreachDetectionService(
                contractRepository, metricRepository, breachRepository, breachPolicy, eventPublisher);

        contract = SlaContract.builder()
                .contractId("contract-001")
                .contractName("Test Contract")
                .vendorId("vendor-001")
                .regionCode("US-EAST")
                .responseTimeThreshold(120)
                .resolutionTimeThreshold(360)
                .availabilityTarget(99.9)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .status(VendorSlaStatus.MET)
                .createdTime(LocalDateTime.now())
                .build();

        metricSnapshot = new SlaMetricSnapshot(
                "snap-001", "contract-001", "response_time",
                200.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET
        );
    }

    @Test
    void detect_shouldDetectBreach_whenMetricExceedsThreshold() {
        when(contractRepository.findById("contract-001")).thenReturn(contract);
        when(metricRepository.findByContractId("contract-001")).thenReturn(List.of(metricSnapshot));
        when(breachRepository.findByContractId("contract-001")).thenReturn(List.of());
        when(breachRepository.save(any(SlaBreach.class))).thenAnswer(i -> i.getArgument(0));
        when(contractRepository.save(any(SlaContract.class))).thenAnswer(i -> i.getArgument(0));

        List<SlaBreach> breaches = breachDetectionService.detect("contract-001");

        assertFalse(breaches.isEmpty());
        assertEquals(1, breaches.size());
        assertEquals("response_time", breaches.get(0).getMetricType());
        assertEquals(200.0, breaches.get(0).getActualValue());
        assertEquals(VendorSlaStatus.BREACHED, breaches.get(0).getStatus());
    }

    @Test
    void detect_shouldNotDetectBreach_whenMetricWithinThreshold() {
        SlaMetricSnapshot okMetric = new SlaMetricSnapshot(
                "snap-002", "contract-001", "response_time",
                100.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET
        );

        when(contractRepository.findById("contract-001")).thenReturn(contract);
        when(metricRepository.findByContractId("contract-001")).thenReturn(List.of(okMetric));

        List<SlaBreach> breaches = breachDetectionService.detect("contract-001");

        assertTrue(breaches.isEmpty());
    }

    @Test
    void detect_shouldSkip_whenContractIsPending() {
        SlaContract pendingContract = SlaContract.builder()
                .contractId("contract-002")
                .contractName("Pending Contract")
                .vendorId("vendor-002")
                .regionCode("US-WEST")
                .responseTimeThreshold(120)
                .resolutionTimeThreshold(360)
                .availabilityTarget(99.9)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .status(VendorSlaStatus.PENDING)
                .createdTime(LocalDateTime.now())
                .build();

        when(contractRepository.findById("contract-002")).thenReturn(pendingContract);

        List<SlaBreach> breaches = breachDetectionService.detect("contract-002");

        assertTrue(breaches.isEmpty());
    }

    @Test
    void resolveBreach_shouldUpdateStatus() {
        SlaBreach breach = SlaBreach.builder()
                .breachId("breach-001")
                .contractId("contract-001")
                .metricType("response_time")
                .actualValue(200.0)
                .threshold(120.0)
                .severity(Severity.MAJOR)
                .status(VendorSlaStatus.BREACHED)
                .detectedTime(LocalDateTime.now())
                .build();

        when(breachRepository.findById("breach-001")).thenReturn(breach);
        when(breachRepository.save(any(SlaBreach.class))).thenAnswer(i -> i.getArgument(0));

        SlaBreach resolved = breachDetectionService.resolveBreach("breach-001");

        assertNotNull(resolved);
        assertEquals(VendorSlaStatus.MET, resolved.getStatus());
        assertNotNull(resolved.getResolvedTime());
    }

    @Test
    void getActiveBreaches_shouldReturnOnlyBreached() {
        SlaBreach activeBreach = SlaBreach.builder()
                .breachId("breach-001")
                .contractId("contract-001")
                .metricType("response_time")
                .actualValue(200.0)
                .threshold(120.0)
                .severity(Severity.MAJOR)
                .status(VendorSlaStatus.BREACHED)
                .detectedTime(LocalDateTime.now())
                .build();

        when(breachRepository.findActiveBreaches()).thenReturn(List.of(activeBreach));

        List<SlaBreach> active = breachDetectionService.getActiveBreaches();

        assertFalse(active.isEmpty());
        assertEquals(1, active.size());
        assertEquals(VendorSlaStatus.BREACHED, active.get(0).getStatus());
    }

    @Test
    void getBreachSummary_shouldCalculateCorrectCounts() {
        SlaBreach breach1 = SlaBreach.builder()
                .breachId("breach-001")
                .contractId("contract-001")
                .metricType("response_time")
                .actualValue(200.0)
                .threshold(120.0)
                .severity(Severity.CRITICAL)
                .status(VendorSlaStatus.BREACHED)
                .detectedTime(LocalDateTime.now())
                .build();
        SlaBreach breach2 = SlaBreach.builder()
                .breachId("breach-002")
                .contractId("contract-001")
                .metricType("resolution_time")
                .actualValue(500.0)
                .threshold(360.0)
                .severity(Severity.MAJOR)
                .status(VendorSlaStatus.MET)
                .detectedTime(LocalDateTime.now())
                .resolvedTime(LocalDateTime.now())
                .build();

        when(breachRepository.findAll()).thenReturn(List.of(breach1, breach2));

        Map<String, Long> summary = breachDetectionService.getBreachSummary();

        assertEquals(2, summary.get("total"));
        assertEquals(1, summary.get("active"));
        assertEquals(1, summary.get("resolved"));
        assertEquals(1, summary.get("critical"));
        assertEquals(1, summary.get("major"));
    }
}
