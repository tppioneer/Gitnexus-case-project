package com.example.telecom.vendor;

import com.example.telecom.vendor.domain.VendorFeedback;
import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.dto.VendorPerformanceReport;
import com.example.telecom.vendor.evaluation.*;
import com.example.telecom.vendor.repository.VendorFeedbackRepository;
import com.example.telecom.vendor.repository.VendorPerformanceRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.service.VendorEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorEvaluationServiceTest {

    @Mock
    private VendorTicketRepository ticketRepository;

    @Mock
    private VendorFeedbackRepository feedbackRepository;

    @Mock
    private VendorPerformanceRepository performanceRepository;

    private VendorEvaluatorRegistry registry;
    private VendorEvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        registry = new VendorEvaluatorRegistry();

        ResponseTimeVendorEvaluator responseTimeEvaluator =
                new ResponseTimeVendorEvaluator(ticketRepository);
        ResolutionRateVendorEvaluator resolutionRateEvaluator =
                new ResolutionRateVendorEvaluator(ticketRepository);
        QualityScoreVendorEvaluator qualityScoreEvaluator =
                new QualityScoreVendorEvaluator(feedbackRepository);

        registry.register(responseTimeEvaluator);
        registry.register(resolutionRateEvaluator);
        registry.register(qualityScoreEvaluator);

        evaluationService = new VendorEvaluationService(registry, performanceRepository);
    }

    @Test
    void shouldEvaluateVendorWithAllEvaluators() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(10));
        ticket.setAcknowledgedTime(LocalDateTime.now().minusHours(2));
        ticket.setResolvedTime(LocalDateTime.now());

        when(ticketRepository.findByVendorId("VENDOR-001")).thenReturn(List.of(ticket));

        VendorFeedback feedback = new VendorFeedback(
                "FB-001", "TICKET-001", "VENDOR-001",
                8, "Good work", "user", "TECHNICAL"
        );

        when(feedbackRepository.findByVendorId("VENDOR-001")).thenReturn(List.of(feedback));

        Map<String, Object> evaluation = evaluationService.evaluateVendor("VENDOR-001");

        assertNotNull(evaluation);
        assertEquals("VENDOR-001", evaluation.get("vendorId"));
        assertNotNull(evaluation.get("overallScore"));
        assertNotNull(evaluation.get("rating"));
        assertNotNull(evaluation.get("evaluations"));

        Map<String, Object> evaluations = (Map<String, Object>) evaluation.get("evaluations");
        assertTrue(evaluations.containsKey("RESPONSE_TIME"));
        assertTrue(evaluations.containsKey("RESOLUTION_RATE"));
        assertTrue(evaluations.containsKey("QUALITY_SCORE"));

        assertNotNull(evaluation.get("strengths"));
        assertNotNull(evaluation.get("weaknesses"));
    }

    @Test
    void shouldReturnEvaluationScore() {
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

        Optional<Double> score = evaluationService.getEvaluationScore("VENDOR-001");

        assertTrue(score.isPresent());
        assertTrue(score.get() >= 0);
        assertTrue(score.get() <= 10);
    }

    @Test
    void shouldReturnScoreForUnknownVendor() {
        when(ticketRepository.findByVendorId("UNKNOWN")).thenReturn(List.of());
        when(feedbackRepository.findByVendorId("UNKNOWN")).thenReturn(List.of());

        Optional<Double> score = evaluationService.getEvaluationScore("UNKNOWN");

        assertTrue(score.isPresent());
    }

    @Test
    void shouldThrowExceptionForBlankVendorId() {
        assertThrows(IllegalArgumentException.class,
                () -> evaluationService.evaluateVendor(""));
        assertThrows(IllegalArgumentException.class,
                () -> evaluationService.evaluateVendor(null));
    }

    @Test
    void shouldGetVendorRanking() {
        VendorPerformanceReport report1 = new VendorPerformanceReport(
                "RPT-001", "VENDOR-001", 2.5, 4.0,
                0.95, 8.0, 8.5, 50,
                LocalDate.now().minusDays(30), LocalDate.now()
        );
        VendorPerformanceReport report2 = new VendorPerformanceReport(
                "RPT-002", "VENDOR-002", 3.0, 5.0,
                0.85, 7.0, 7.2, 30,
                LocalDate.now().minusDays(30), LocalDate.now()
        );

        when(performanceRepository.findAll()).thenReturn(List.of(report1, report2));

        List<Map<String, Object>> rankings = evaluationService.getVendorRanking();

        assertNotNull(rankings);
        assertEquals(2, rankings.size());
        assertEquals("VENDOR-001", rankings.get(0).get("vendorId"));
        assertEquals(1, rankings.get(0).get("rank"));
        assertEquals("VENDOR-002", rankings.get(1).get("vendorId"));
        assertEquals(2, rankings.get(1).get("rank"));
    }

    @Test
    void shouldReturnEmptyRankingWhenNoReports() {
        when(performanceRepository.findAll()).thenReturn(List.of());

        List<Map<String, Object>> rankings = evaluationService.getVendorRanking();

        assertNotNull(rankings);
        assertTrue(rankings.isEmpty());
    }

    @Test
    void shouldGetDetailedEvaluation() {
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

        Map<String, Object> detailed = evaluationService.getDetailedEvaluation("VENDOR-001");

        assertNotNull(detailed);
        assertNotNull(detailed.get("detailedMetrics"));
        assertNotNull(detailed.get("evaluationTimestamp"));
    }
}
