package com.example.telecom.vendor;

import com.example.telecom.vendor.domain.VendorFeedback;
import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.evaluation.*;
import com.example.telecom.vendor.repository.VendorFeedbackRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorEvaluatorRegistryTest {

    @Mock
    private VendorTicketRepository ticketRepository;

    @Mock
    private VendorFeedbackRepository feedbackRepository;

    private VendorEvaluatorRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new VendorEvaluatorRegistry();
    }

    @Test
    void shouldRegisterAndResolveEvaluators() {
        ResponseTimeVendorEvaluator responseTimeEvaluator =
                new ResponseTimeVendorEvaluator(ticketRepository);
        ResolutionRateVendorEvaluator resolutionRateEvaluator =
                new ResolutionRateVendorEvaluator(ticketRepository);
        QualityScoreVendorEvaluator qualityScoreEvaluator =
                new QualityScoreVendorEvaluator(feedbackRepository);

        registry.register(responseTimeEvaluator);
        registry.register(resolutionRateEvaluator);
        registry.register(qualityScoreEvaluator);

        Optional<VendorEvaluator> resolved = registry.resolve("RESPONSE_TIME");
        assertTrue(resolved.isPresent());
        assertEquals("RESPONSE_TIME", resolved.get().getEvaluatorType());

        resolved = registry.resolve("RESOLUTION_RATE");
        assertTrue(resolved.isPresent());
        assertEquals("RESOLUTION_RATE", resolved.get().getEvaluatorType());

        resolved = registry.resolve("QUALITY_SCORE");
        assertTrue(resolved.isPresent());
        assertEquals("QUALITY_SCORE", resolved.get().getEvaluatorType());

        assertEquals(3, registry.getAllEvaluators().size());
    }

    @Test
    void shouldReturnEmptyForUnregisteredEvaluator() {
        Optional<VendorEvaluator> resolved = registry.resolve("NONEXISTENT");
        assertTrue(resolved.isEmpty());
    }

    @Test
    void shouldReturnEmptyForNullEvaluatorType() {
        Optional<VendorEvaluator> resolved = registry.resolve(null);
        assertTrue(resolved.isEmpty());
    }

    @Test
    void shouldEvaluateAllRegisteredEvaluators() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.CLOSED, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(5));
        ticket.setAcknowledgedTime(LocalDateTime.now().minusHours(1));
        ticket.setResolvedTime(LocalDateTime.now().minusHours(2));

        when(ticketRepository.findByVendorId("VENDOR-001")).thenReturn(List.of(ticket));
        when(feedbackRepository.findByVendorId("VENDOR-001")).thenReturn(List.of());

        ResponseTimeVendorEvaluator responseTimeEvaluator =
                new ResponseTimeVendorEvaluator(ticketRepository);
        ResolutionRateVendorEvaluator resolutionRateEvaluator =
                new ResolutionRateVendorEvaluator(ticketRepository);
        QualityScoreVendorEvaluator qualityScoreEvaluator =
                new QualityScoreVendorEvaluator(feedbackRepository);

        registry.register(responseTimeEvaluator);
        registry.register(resolutionRateEvaluator);
        registry.register(qualityScoreEvaluator);

        List<EvaluationResult> results = registry.evaluateAll("VENDOR-001");

        assertNotNull(results);
        assertEquals(3, results.size());

        for (EvaluationResult result : results) {
            assertNotNull(result.getEvaluatorType());
            assertTrue(result.getScore() >= 0);
            assertTrue(result.getScore() <= 10);
            assertNotNull(result.getDescription());
            assertNotNull(result.getMetrics());
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoEvaluatorsRegistered() {
        List<EvaluationResult> results = registry.evaluateAll("VENDOR-001");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldOverrideExistingEvaluatorOnReRegister() {
        VendorEvaluator evaluator1 = new ResponseTimeVendorEvaluator(ticketRepository);
        VendorEvaluator evaluator2 = new ResolutionRateVendorEvaluator(ticketRepository);

        registry.register(evaluator1);
        assertEquals(1, registry.getAllEvaluators().size());

        registry.register(evaluator2);
        assertEquals(2, registry.getAllEvaluators().size());

        registry.register(evaluator1);
        assertEquals(2, registry.getAllEvaluators().size());
    }

    @Test
    void shouldReturnResponseTimeMetrics() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(5));
        ticket.setAcknowledgedTime(LocalDateTime.now().minusHours(1));

        when(ticketRepository.findByVendorId("VENDOR-001")).thenReturn(List.of(ticket));

        ResponseTimeVendorEvaluator evaluator = new ResponseTimeVendorEvaluator(ticketRepository);
        Map<String, Object> metrics = evaluator.getResponseTimeMetrics("VENDOR-001");

        assertNotNull(metrics);
        assertEquals("VENDOR-001", metrics.get("vendorId"));
        assertNotNull(metrics.get("averageResponseTimeHours"));
    }

    @Test
    void shouldReturnResolutionMetrics() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.CLOSED, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(5));
        ticket.setResolvedTime(LocalDateTime.now());

        when(ticketRepository.findByVendorId("VENDOR-001")).thenReturn(List.of(ticket));

        ResolutionRateVendorEvaluator evaluator = new ResolutionRateVendorEvaluator(ticketRepository);
        Map<String, Object> metrics = evaluator.getResolutionMetrics("VENDOR-001");

        assertNotNull(metrics);
        assertEquals("VENDOR-001", metrics.get("vendorId"));
        assertEquals(1L, metrics.get("resolvedTickets"));
    }

    @Test
    void shouldReturnQualityMetrics() {
        VendorFeedback feedback = new VendorFeedback(
                "FB-001", "TICKET-001", "VENDOR-001",
                8, "Good", "user", "TECHNICAL"
        );

        when(feedbackRepository.findByVendorId("VENDOR-001")).thenReturn(List.of(feedback));

        QualityScoreVendorEvaluator evaluator = new QualityScoreVendorEvaluator(feedbackRepository);
        Map<String, Object> metrics = evaluator.getQualityMetrics("VENDOR-001");

        assertNotNull(metrics);
        assertEquals("VENDOR-001", metrics.get("vendorId"));
        assertNotNull(metrics.get("averageScore"));
        assertNotNull(metrics.get("medianScore"));
    }
}
