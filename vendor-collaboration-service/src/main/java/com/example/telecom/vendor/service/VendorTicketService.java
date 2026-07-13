package com.example.telecom.vendor.service;

import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.dto.VendorTicketRequest;
import com.example.telecom.vendor.dto.VendorTicketResponse;
import com.example.telecom.vendor.event.VendorTicketEventPublisher;
import com.example.telecom.vendor.mapper.VendorTicketMapper;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.workflow.VendorCollaborationStateMachine;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VendorTicketService {

    private final VendorTicketRepository ticketRepository;
    private final VendorTicketEventPublisher eventPublisher;
    private final VendorCollaborationStateMachine stateMachine;

    public VendorTicketService(VendorTicketRepository ticketRepository,
                               VendorTicketEventPublisher eventPublisher,
                               VendorCollaborationStateMachine stateMachine) {
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
        this.stateMachine = stateMachine;
    }

    public VendorTicketResponse createTicket(VendorTicketRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ValidationException("title", "Title is required");
        }
        if (request.getVendorId() == null || request.getVendorId().isBlank()) {
            throw new ValidationException("vendorId", "Vendor ID is required");
        }
        if (request.getVendorName() == null || request.getVendorName().isBlank()) {
            throw new ValidationException("vendorName", "Vendor name is required");
        }

        String ticketId = UUID.randomUUID().toString();
        VendorTicket ticket = new VendorTicket(
                ticketId,
                request.getTitle(),
                request.getDescription(),
                request.getVendorId(),
                request.getVendorName(),
                request.getDeviceId(),
                VendorTicketStatus.CREATED,
                request.getPriority() != null ? request.getPriority() : "MEDIUM",
                request.getRegionCode()
        );

        VendorTicket saved = ticketRepository.save(ticket);
        eventPublisher.publishCreated(saved);
        return VendorTicketMapper.toResponse(saved);
    }

    public Optional<VendorTicketResponse> getTicket(String ticketId) {
        return ticketRepository.findById(ticketId)
                .map(VendorTicketMapper::toResponse);
    }

    public List<VendorTicketResponse> listTickets(VendorTicketStatus status, String vendorId,
                                                   LocalDate dateFrom, LocalDate dateTo) {
        List<VendorTicket> tickets;

        if (vendorId != null && dateFrom != null && dateTo != null) {
            tickets = ticketRepository.findByVendorIdAndDateRange(vendorId, dateFrom, dateTo);
        } else if (vendorId != null) {
            tickets = ticketRepository.findByVendorId(vendorId);
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else if (dateFrom != null && dateTo != null) {
            tickets = ticketRepository.findByDateRange(dateFrom, dateTo);
        } else {
            tickets = ticketRepository.findAll();
        }

        return tickets.stream()
                .map(VendorTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VendorTicketResponse acknowledgeTicket(String ticketId) {
        VendorTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DomainException("TICKET_NOT_FOUND", "Ticket not found: " + ticketId));

        validateTransition(ticketId, ticket.getStatus(), VendorTicketStatus.VENDOR_ACKED);
        ticket.setStatus(VendorTicketStatus.VENDOR_ACKED);
        ticket.setAcknowledgedTime(LocalDateTime.now());
        ticketRepository.save(ticket);
        eventPublisher.publishAcknowledged(ticket);
        return VendorTicketMapper.toResponse(ticket);
    }

    public VendorTicketResponse markInProgress(String ticketId) {
        VendorTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DomainException("TICKET_NOT_FOUND", "Ticket not found: " + ticketId));

        validateTransition(ticketId, ticket.getStatus(), VendorTicketStatus.VENDOR_IN_PROGRESS);
        ticket.setStatus(VendorTicketStatus.VENDOR_IN_PROGRESS);
        ticket.setInProgressTime(LocalDateTime.now());
        ticketRepository.save(ticket);
        eventPublisher.publishInProgress(ticket);
        return VendorTicketMapper.toResponse(ticket);
    }

    public VendorTicketResponse resolveTicket(String ticketId) {
        VendorTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DomainException("TICKET_NOT_FOUND", "Ticket not found: " + ticketId));

        validateTransition(ticketId, ticket.getStatus(), VendorTicketStatus.VENDOR_RESOLVED);
        ticket.setStatus(VendorTicketStatus.VENDOR_RESOLVED);
        ticket.setResolvedTime(LocalDateTime.now());
        ticketRepository.save(ticket);
        eventPublisher.publishResolved(ticket);
        return VendorTicketMapper.toResponse(ticket);
    }

    public VendorTicketResponse closeTicket(String ticketId) {
        VendorTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DomainException("TICKET_NOT_FOUND", "Ticket not found: " + ticketId));

        validateTransition(ticketId, ticket.getStatus(), VendorTicketStatus.CLOSED);
        ticket.setStatus(VendorTicketStatus.CLOSED);
        ticket.setClosedTime(LocalDateTime.now());
        ticketRepository.save(ticket);
        eventPublisher.publishClosed(ticket);
        return VendorTicketMapper.toResponse(ticket);
    }

    public VendorTicketResponse escalateTicket(String ticketId) {
        VendorTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DomainException("TICKET_NOT_FOUND", "Ticket not found: " + ticketId));

        validateTransition(ticketId, ticket.getStatus(), VendorTicketStatus.ESCALATED);
        ticket.setStatus(VendorTicketStatus.ESCALATED);
        ticketRepository.save(ticket);
        eventPublisher.publishEscalated(ticket, "Manual escalation");
        return VendorTicketMapper.toResponse(ticket);
    }

    public long countTicketsByVendor(String vendorId) {
        return ticketRepository.countByVendorId(vendorId);
    }

    public long countTicketsByStatus(VendorTicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public List<VendorTicketResponse> getOpenTickets() {
        return ticketRepository.findOpenTickets().stream()
                .map(VendorTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<VendorTicketResponse> getEscalatedTickets() {
        return ticketRepository.findEscalated().stream()
                .map(VendorTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateTransition(String ticketId, VendorTicketStatus from, VendorTicketStatus to) {
        if (!stateMachine.isValidTransition(from, to)) {
            throw new DomainException("INVALID_TRANSITION",
                    "Cannot transition ticket " + ticketId + " from " + from + " to " + to);
        }
    }
}
