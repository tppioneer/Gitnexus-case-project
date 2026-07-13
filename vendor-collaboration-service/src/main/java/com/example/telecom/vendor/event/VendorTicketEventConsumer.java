package com.example.telecom.vendor.event;

import com.example.telecom.vendor.service.VendorNotificationService;
import com.example.telecom.vendor.service.VendorSlaTrackingService;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

public class VendorTicketEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(VendorTicketEventConsumer.class);

    private final VendorTicketRepository ticketRepository;
    private final VendorSlaTrackingService slaTrackingService;
    private final VendorNotificationService notificationService;

    public VendorTicketEventConsumer(VendorTicketRepository ticketRepository,
                                     VendorSlaTrackingService slaTrackingService,
                                     VendorNotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.slaTrackingService = slaTrackingService;
        this.notificationService = notificationService;
    }

    @EventListener
    public void onVendorTicketCreated(VendorTicketEvent event) {
        if (event.getEventType() != VendorTicketEvent.EventType.CREATED) {
            return;
        }
        log.info("Processing ticket created event for ticket: {}", event.getTicketId());
        log.info("Event details: {}", event.getDetails());
        ticketRepository.findById(event.getTicketId()).ifPresent(ticket -> {
            slaTrackingService.track(ticket.getTicketId());
            notificationService.notifyTicketCreated(ticket);
            log.info("Ticket {} tracking started and notification sent", ticket.getTicketId());
        });
    }

    @EventListener
    public void onVendorTicketResolved(VendorTicketEvent event) {
        if (event.getEventType() != VendorTicketEvent.EventType.RESOLVED) {
            return;
        }
        log.info("Processing ticket resolved event for ticket: {}", event.getTicketId());
        ticketRepository.findById(event.getTicketId()).ifPresent(ticket -> {
            slaTrackingService.updateSlaMetrics(ticket.getTicketId());
            log.info("SLA metrics updated for resolved ticket {}", ticket.getTicketId());
        });
    }

    @EventListener
    public void onVendorTicketEscalated(VendorTicketEvent event) {
        if (event.getEventType() != VendorTicketEvent.EventType.ESCALATED) {
            return;
        }
        log.info("Processing ticket escalated event for ticket: {}", event.getTicketId());
        log.info("Escalation reason: {}", event.getDetails());
        ticketRepository.findById(event.getTicketId()).ifPresent(ticket -> {
            notificationService.notifyEscalation(ticket, event.getDetails());
            log.info("Escalation notification sent for ticket {}", ticket.getTicketId());
        });
    }

    @EventListener
    public void onVendorTicketAcknowledged(VendorTicketEvent event) {
        if (event.getEventType() != VendorTicketEvent.EventType.ACKNOWLEDGED) {
            return;
        }
        log.info("Processing ticket acknowledged event for ticket: {}", event.getTicketId());
        ticketRepository.findById(event.getTicketId()).ifPresent(ticket -> {
            slaTrackingService.updateSlaMetrics(ticket.getTicketId());
            log.info("SLA metrics updated for acknowledged ticket {}", ticket.getTicketId());
        });
    }

    @EventListener
    public void onVendorTicketClosed(VendorTicketEvent event) {
        if (event.getEventType() != VendorTicketEvent.EventType.CLOSED) {
            return;
        }
        log.info("Processing ticket closed event for ticket: {}", event.getTicketId());
        ticketRepository.findById(event.getTicketId()).ifPresent(ticket -> {
            slaTrackingService.updateSlaMetrics(ticket.getTicketId());
            log.info("SLA metrics finalized for closed ticket {}", ticket.getTicketId());
        });
    }
}
