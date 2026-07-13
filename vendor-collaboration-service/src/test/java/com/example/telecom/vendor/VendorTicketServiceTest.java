package com.example.telecom.vendor;

import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.dto.VendorTicketRequest;
import com.example.telecom.vendor.dto.VendorTicketResponse;
import com.example.telecom.vendor.event.VendorTicketEventPublisher;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.service.VendorTicketService;
import com.example.telecom.vendor.workflow.VendorCollaborationStateMachine;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
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
class VendorTicketServiceTest {

    @Mock
    private VendorTicketRepository ticketRepository;

    @Mock
    private VendorTicketEventPublisher eventPublisher;

    private VendorCollaborationStateMachine stateMachine;
    private VendorTicketService ticketService;

    @BeforeEach
    void setUp() {
        stateMachine = new VendorCollaborationStateMachine();
        ticketService = new VendorTicketService(ticketRepository, eventPublisher, stateMachine);
    }

    @Test
    void shouldCreateTicketSuccessfully() {
        VendorTicketRequest request = new VendorTicketRequest(
                "Network outage", "Fiber cut in sector A",
                "VENDOR-001", "FiberTech Solutions",
                "DEVICE-001", "HIGH", "REG-001"
        );

        VendorTicket savedTicket = new VendorTicket(
                "TICKET-001", "Network outage", "Fiber cut in sector A",
                "VENDOR-001", "FiberTech Solutions",
                "DEVICE-001", VendorTicketStatus.CREATED, "HIGH", "REG-001"
        );

        when(ticketRepository.save(any(VendorTicket.class))).thenReturn(savedTicket);

        VendorTicketResponse response = ticketService.createTicket(request);

        assertNotNull(response);
        assertEquals("Network outage", response.getTitle());
        assertEquals("VENDOR-001", response.getVendorId());
        assertEquals(VendorTicketStatus.CREATED, response.getStatus());
        assertNotNull(response.getCreatedTime());
        verify(ticketRepository, times(1)).save(any(VendorTicket.class));
        verify(eventPublisher, times(1)).publishCreated(any(VendorTicket.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenTitleIsMissing() {
        VendorTicketRequest request = new VendorTicketRequest(
                "", "Description", "VENDOR-001", "Vendor",
                null, "MEDIUM", null
        );

        assertThrows(ValidationException.class, () -> ticketService.createTicket(request));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenVendorIdIsMissing() {
        VendorTicketRequest request = new VendorTicketRequest(
                "Title", "Description", "", "Vendor",
                null, "MEDIUM", null
        );

        assertThrows(ValidationException.class, () -> ticketService.createTicket(request));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenVendorNameIsMissing() {
        VendorTicketRequest request = new VendorTicketRequest(
                "Title", "Description", "VENDOR-001", "",
                null, "MEDIUM", null
        );

        assertThrows(ValidationException.class, () -> ticketService.createTicket(request));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldDefaultPriorityToMedium() {
        VendorTicketRequest request = new VendorTicketRequest(
                "Network issue", "Description",
                "VENDOR-001", "FiberTech Solutions",
                null, null, null
        );

        VendorTicket savedTicket = new VendorTicket(
                "TICKET-002", "Network issue", "Description",
                "VENDOR-001", "FiberTech Solutions",
                null, VendorTicketStatus.CREATED, "MEDIUM", null
        );

        when(ticketRepository.save(any(VendorTicket.class))).thenReturn(savedTicket);

        VendorTicketResponse response = ticketService.createTicket(request);
        assertNotNull(response);
        assertEquals("MEDIUM", response.getPriority());
    }

    @Test
    void shouldAcknowledgeTicketSuccessfully() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Network issue", "Description",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.AWAITING_VENDOR, "HIGH", null
        );

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(VendorTicket.class))).thenAnswer(i -> i.getArgument(0));

        VendorTicketResponse response = ticketService.acknowledgeTicket("TICKET-001");

        assertNotNull(response);
        assertEquals(VendorTicketStatus.VENDOR_ACKED, response.getStatus());
        assertNotNull(response.getAcknowledgedTime());
        verify(ticketRepository, times(1)).save(ticket);
        verify(eventPublisher, times(1)).publishAcknowledged(ticket);
    }

    @Test
    void shouldMarkInProgressSuccessfully() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Network issue", "Description",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_ACKED, "HIGH", null
        );

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(VendorTicket.class))).thenAnswer(i -> i.getArgument(0));

        VendorTicketResponse response = ticketService.markInProgress("TICKET-001");

        assertNotNull(response);
        assertEquals(VendorTicketStatus.VENDOR_IN_PROGRESS, response.getStatus());
        assertNotNull(response.getInProgressTime());
        verify(eventPublisher, times(1)).publishInProgress(ticket);
    }

    @Test
    void shouldResolveTicketSuccessfully() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Network issue", "Description",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VENDOR_IN_PROGRESS, "HIGH", null
        );

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(VendorTicket.class))).thenAnswer(i -> i.getArgument(0));

        VendorTicketResponse response = ticketService.resolveTicket("TICKET-001");

        assertNotNull(response);
        assertEquals(VendorTicketStatus.VENDOR_RESOLVED, response.getStatus());
        assertNotNull(response.getResolvedTime());
        verify(eventPublisher, times(1)).publishResolved(ticket);
    }

    @Test
    void shouldCloseTicketSuccessfully() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Network issue", "Description",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.VERIFIED, "HIGH", null
        );

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(VendorTicket.class))).thenAnswer(i -> i.getArgument(0));

        VendorTicketResponse response = ticketService.closeTicket("TICKET-001");

        assertNotNull(response);
        assertEquals(VendorTicketStatus.CLOSED, response.getStatus());
        assertNotNull(response.getClosedTime());
        verify(eventPublisher, times(1)).publishClosed(ticket);
    }

    @Test
    void shouldEscalateTicketSuccessfully() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Network issue", "Description",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.AWAITING_VENDOR, "HIGH", null
        );

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(VendorTicket.class))).thenAnswer(i -> i.getArgument(0));

        VendorTicketResponse response = ticketService.escalateTicket("TICKET-001");

        assertNotNull(response);
        assertEquals(VendorTicketStatus.ESCALATED, response.getStatus());
        verify(eventPublisher, times(1)).publishEscalated(ticket, "Manual escalation");
    }

    @Test
    void shouldThrowDomainExceptionForInvalidTransition() {
        VendorTicket ticket = new VendorTicket(
                "TICKET-001", "Network issue", "Description",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.CREATED, "HIGH", null
        );

        when(ticketRepository.findById("TICKET-001")).thenReturn(Optional.of(ticket));

        assertThrows(DomainException.class, () -> ticketService.closeTicket("TICKET-001"));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowDomainExceptionForTicketNotFound() {
        when(ticketRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> ticketService.acknowledgeTicket("NONEXISTENT"));
        assertThrows(DomainException.class, () -> ticketService.resolveTicket("NONEXISTENT"));
    }

    @Test
    void shouldListTicketsByStatus() {
        VendorTicket ticket1 = new VendorTicket(
                "TICKET-001", "Issue 1", "Desc",
                "VENDOR-001", "Vendor", null,
                VendorTicketStatus.AWAITING_VENDOR, "HIGH", null
        );
        VendorTicket ticket2 = new VendorTicket(
                "TICKET-002", "Issue 2", "Desc",
                "VENDOR-002", "Vendor 2", null,
                VendorTicketStatus.AWAITING_VENDOR, "LOW", null
        );

        when(ticketRepository.findByStatus(VendorTicketStatus.AWAITING_VENDOR))
                .thenReturn(List.of(ticket1, ticket2));

        List<VendorTicketResponse> tickets = ticketService.listTickets(
                VendorTicketStatus.AWAITING_VENDOR, null, null, null);

        assertEquals(2, tickets.size());
    }

    @Test
    void shouldReturnEmptyWhenGetTicketNotFound() {
        when(ticketRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<VendorTicketResponse> result = ticketService.getTicket("NONEXISTENT");

        assertTrue(result.isEmpty());
    }
}
