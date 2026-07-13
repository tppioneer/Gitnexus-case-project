package com.example.telecom.vendor;

import com.example.telecom.vendor.config.VendorCollaborationProperties;
import com.example.telecom.vendor.domain.VendorSlaReport;
import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.repository.VendorSlaRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.service.VendorSlaTrackingService;
import com.example.telecom.common.vendor.VendorSlaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorSlaTrackingServiceTest {

    @Mock
    private VendorTicketRepository ticketRepository;

    @Mock
    private VendorSlaRepository slaRepository;

    private VendorCollaborationProperties properties;
    private VendorSlaTrackingService slaTrackingService;

    @BeforeEach
    void setUp() {
        properties = new VendorCollaborationProperties();
        properties.setSlaResponseTimeHours(4);
        properties.setSlaResolutionTimeHours(48);
        slaTrackingService = new VendorSlaTrackingService(ticketRepository, slaRepository, properties);
    }

    @Test
    void shouldReturnMetForTicketResolvedWithinSla() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(10));
        ticket.setResolvedTime(LocalDateTime.now());

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));

        VendorSlaStatus status = slaTrackingService.checkSlaBreach("TICKET-001");

        assertEquals(VendorSlaStatus.MET, status);
    }

    @Test
    void shouldReturnBreachedForTicketExceedingSlaResolutionTime() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(100));
        ticket.setResolvedTime(LocalDateTime.now());

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));

        VendorSlaStatus status = slaTrackingService.checkSlaBreach("TICKET-001");

        assertEquals(VendorSlaStatus.BREACHED, status);
    }

    @Test
    void shouldReturnPendingForTicketInProgress() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_IN_PROGRESS, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(2));

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));

        VendorSlaStatus status = slaTrackingService.checkSlaBreach("TICKET-001");

        assertEquals(VendorSlaStatus.PENDING, status);
    }

    @Test
    void shouldReturnPendingForTicketNotFound() {
        when(ticketRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        VendorSlaStatus status = slaTrackingService.checkSlaBreach("NONEXISTENT");

        assertEquals(VendorSlaStatus.PENDING, status);
    }

    @Test
    void shouldGenerateSlaReport() {
        VendorTicket ticket1 = new VendorTicket(
                "TICKET-001", "Issue 1", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "MEDIUM", null
        );
        ticket1.setCreatedTime(LocalDateTime.now().minusHours(10));
        ticket1.setResolvedTime(LocalDateTime.now());

        VendorTicket ticket2 = new VendorTicket(
                "TICKET-002", "Issue 2", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "HIGH", null
        );
        ticket2.setCreatedTime(LocalDateTime.now().minusHours(100));
        ticket2.setResolvedTime(LocalDateTime.now());

        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        when(ticketRepository.findByVendorIdAndDateRange("VENDOR-001", from, to))
                .thenReturn(List.of(ticket1, ticket2));
        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket1));
        when(ticketRepository.findById("TICKET-002")).thenReturn(Optional.of(ticket2));
        when(slaRepository.save(any(VendorSlaReport.class)))
                .thenAnswer(i -> i.getArgument(0));

        VendorSlaReport report = slaTrackingService.getVendorSlaReport("VENDOR-001", from, to);

        assertNotNull(report);
        assertEquals("VENDOR-001", report.getVendorId());
        assertEquals(2, report.getTotalTickets());
        assertEquals(1, report.getTicketsMet());
        assertEquals(1, report.getTicketsBreached());
        assertTrue(report.getSlaPercentage() > 0);
        assertNotNull(report.getReportId());
        assertNotNull(report.getGeneratedTime());
    }

    @Test
    void shouldGenerateReportWithFullSlaMetWhenNoTickets() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(ticketRepository.findByVendorIdAndDateRange("VENDOR-001", from, to))
                .thenReturn(List.of());
        when(slaRepository.save(any(VendorSlaReport.class)))
                .thenAnswer(i -> i.getArgument(0));

        VendorSlaReport report = slaTrackingService.getVendorSlaReport("VENDOR-001", from, to);

        assertNotNull(report);
        assertEquals(100.0, report.getSlaPercentage(), 0.01);
        assertEquals(VendorSlaStatus.MET, report.getSlaStatus());
        assertEquals(0, report.getTotalTickets());
    }

    @Test
    void shouldCalculateSlaPercentage() {
        VendorTicket ticket1 = new VendorTicket(
                "TICKET-001", "Issue 1", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "MEDIUM", null
        );
        ticket1.setCreatedTime(LocalDateTime.now().minusHours(10));
        ticket1.setResolvedTime(LocalDateTime.now());

        VendorTicket ticket2 = new VendorTicket(
                "TICKET-002", "Issue 2", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_RESOLVED, "HIGH", null
        );
        ticket2.setCreatedTime(LocalDateTime.now().minusHours(100));
        ticket2.setResolvedTime(LocalDateTime.now());

        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        when(ticketRepository.findByVendorIdAndDateRange("VENDOR-001", from, to))
                .thenReturn(List.of(ticket1, ticket2));

        double percentage = slaTrackingService.calculateSlaPercentage("VENDOR-001", from, to);

        assertEquals(50.0, percentage, 0.01);
    }

    @Test
    void shouldReturnFullPercentageWhenNoTickets() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(ticketRepository.findByVendorIdAndDateRange("VENDOR-001", from, to))
                .thenReturn(List.of());

        double percentage = slaTrackingService.calculateSlaPercentage("VENDOR-001", from, to);

        assertEquals(100.0, percentage, 0.01);
    }

    @Test
    void shouldTrackTicketAndCheckSla() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Issue", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_IN_PROGRESS, "MEDIUM", null
        );
        ticket.setCreatedTime(LocalDateTime.now().minusHours(2));

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));

        assertDoesNotThrow(() -> slaTrackingService.track("TICKET-001"));
        verify(ticketRepository, atLeastOnce()).findById("TICKET-001");
    }
}
