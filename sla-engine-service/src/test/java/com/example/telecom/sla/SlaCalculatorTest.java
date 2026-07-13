package com.example.telecom.sla;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.calculator.*;
import com.example.telecom.sla.domain.SlaCalculationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SlaCalculatorTest {

    private SlaTimeCalculator timeCalculator;
    private SlaAvailabilityCalculator availabilityCalculator;
    private SlaResponseTimeCalculator responseTimeCalculator;
    private SlaResolutionTimeCalculator resolutionTimeCalculator;
    private SlaCalculatorRegistry registry;

    private SlaContract contract;
    private List<SlaMetricSnapshot> metrics;

    @BeforeEach
    void setUp() {
        timeCalculator = new SlaTimeCalculator();
        availabilityCalculator = new SlaAvailabilityCalculator();
        responseTimeCalculator = new SlaResponseTimeCalculator();
        resolutionTimeCalculator = new SlaResolutionTimeCalculator();
        registry = new SlaCalculatorRegistry();

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

        metrics = new ArrayList<>();
        metrics.add(new SlaMetricSnapshot("snap-001", "contract-001", "response_time",
                95.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET));
        metrics.add(new SlaMetricSnapshot("snap-002", "contract-001", "response_time",
                110.0, 120.0, LocalDateTime.now(), VendorSlaStatus.MET));
        metrics.add(new SlaMetricSnapshot("snap-003", "contract-001", "resolution_time",
                200.0, 360.0, LocalDateTime.now(), VendorSlaStatus.MET));
        metrics.add(new SlaMetricSnapshot("snap-004", "contract-001", "availability",
                99.8, 99.5, LocalDateTime.now(), VendorSlaStatus.MET));
    }

    @Test
    void timeCalculator_shouldCalculateCorrectly() {
        SlaCalculationResult result = timeCalculator.calculate(contract, metrics);

        assertNotNull(result);
        assertEquals("time", result.getCalculatorType());
        assertTrue(result.isPassed());

        Map<String, Object> details = result.getDetails();
        assertTrue((Double) details.get("responseTime") <= 120);
        assertTrue((Double) details.get("resolutionTime") <= 360);
    }

    @Test
    void availabilityCalculator_shouldCalculateCorrectly() {
        SlaCalculationResult result = availabilityCalculator.calculate(contract, metrics);

        assertNotNull(result);
        assertEquals("availability", result.getCalculatorType());
        assertTrue(result.isPassed());
        assertTrue(result.getMetricValue() >= 99.5);
    }

    @Test
    void responseTimeCalculator_shouldCalculatePercentiles() {
        SlaCalculationResult result = responseTimeCalculator.calculate(contract, metrics);

        assertNotNull(result);
        assertEquals("response_time", result.getCalculatorType());
        assertTrue(result.isPassed());

        double p95 = responseTimeCalculator.calculatePercentile(metrics, 95);
        assertTrue(p95 > 0);
    }

    @Test
    void registry_shouldRouteCalculations() {
        registry.register("time", timeCalculator);
        registry.register("availability", availabilityCalculator);
        registry.register("response_time", responseTimeCalculator);
        registry.register("resolution_time", resolutionTimeCalculator);

        List<SlaCalculationResult> results = registry.calculateAll(contract, metrics);

        assertNotNull(results);
        assertEquals(4, results.size());

        SlaCalculator resolved = registry.resolve("time");
        assertNotNull(resolved);
        assertTrue(resolved instanceof SlaTimeCalculator);
    }
}
