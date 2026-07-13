package com.example.telecom.vendor.event;

import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

public class VendorTicketEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public VendorTicketEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishCreated(VendorTicket ticket) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                VendorTicketEvent.EventType.CREATED,
                null,
                VendorTicketStatus.CREATED,
                "Ticket created: " + ticket.getTitle()
        );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishAcknowledged(VendorTicket ticket) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                VendorTicketEvent.EventType.ACKNOWLEDGED,
                VendorTicketStatus.AWAITING_VENDOR,
                VendorTicketStatus.VENDOR_ACKED,
                "Ticket acknowledged by vendor"
        );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishInProgress(VendorTicket ticket) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                VendorTicketEvent.EventType.IN_PROGRESS,
                VendorTicketStatus.VENDOR_ACKED,
                VendorTicketStatus.VENDOR_IN_PROGRESS,
                "Ticket is in progress"
        );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishResolved(VendorTicket ticket) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                VendorTicketEvent.EventType.RESOLVED,
                VendorTicketStatus.VENDOR_IN_PROGRESS,
                VendorTicketStatus.VENDOR_RESOLVED,
                "Ticket resolved by vendor"
        );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishEscalated(VendorTicket ticket, String reason) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                VendorTicketEvent.EventType.ESCALATED,
                ticket.getStatus(),
                VendorTicketStatus.ESCALATED,
                "Ticket escalated: " + reason
        );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishClosed(VendorTicket ticket) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                VendorTicketEvent.EventType.CLOSED,
                VendorTicketStatus.VERIFIED,
                VendorTicketStatus.CLOSED,
                "Ticket closed"
        );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishEvent(VendorTicket ticket, VendorTicketEvent.EventType eventType, String details) {
        VendorTicketEvent event = new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                eventType,
                ticket.getStatus(),
                ticket.getStatus(),
                details
        );
        applicationEventPublisher.publishEvent(event);
    }
}
