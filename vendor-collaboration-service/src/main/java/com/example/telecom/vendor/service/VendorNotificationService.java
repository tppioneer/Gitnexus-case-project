package com.example.telecom.vendor.service;

import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VendorNotificationService {

    private static final Logger log = LoggerFactory.getLogger(VendorNotificationService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VendorTicketRepository ticketRepository;

    public VendorNotificationService(VendorTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void notifyTicketCreated(VendorTicket ticket) {
        log.info("=== New Ticket Notification ===");
        log.info("Ticket ID: {}", ticket.getTicketId());
        log.info("Title: {}", ticket.getTitle());
        log.info("Vendor: {} ({})", ticket.getVendorName(), ticket.getVendorId());
        log.info("Priority: {}", ticket.getPriority());
        log.info("Created: {}", ticket.getCreatedTime().format(FORMATTER));
        log.info("Region: {}", ticket.getRegionCode());
        log.info("=== End of Notification ===");
    }

    public void notifySlaBreach(VendorTicket ticket) {
        log.warn("!!! SLA BREACH ALERT !!!");
        log.warn("Ticket ID: {}", ticket.getTicketId());
        log.warn("Title: {}", ticket.getTitle());
        log.warn("Vendor: {} ({})", ticket.getVendorName(), ticket.getVendorId());
        log.warn("Current Status: {}", ticket.getStatus());
        log.warn("Created: {}", ticket.getCreatedTime().format(FORMATTER));
        log.warn("Acknowledged: {}",
                ticket.getAcknowledgedTime() != null
                        ? ticket.getAcknowledgedTime().format(FORMATTER)
                        : "Not yet acknowledged");
        log.warn("SLA Response Time Limit exceeded");
        log.warn("!!! END OF SLA BREACH ALERT !!!");
    }

    public void notifyEscalation(VendorTicket ticket, String reason) {
        log.warn("=== ESCALATION NOTIFICATION ===");
        log.warn("Ticket ID: {}", ticket.getTicketId());
        log.warn("Title: {}", ticket.getTitle());
        log.warn("Vendor: {} ({})", ticket.getVendorName(), ticket.getVendorId());
        log.warn("Escalation Reason: {}", reason);
        log.warn("Previous Status: {}", ticket.getStatus());
        log.warn("Escalation Time: {}", LocalDateTime.now().format(FORMATTER));
        log.warn("=== END OF ESCALATION NOTIFICATION ===");
    }

    public void sendDigest(String vendorId) {
        List<VendorTicket> vendorTickets = ticketRepository.findByVendorId(vendorId);

        if (vendorTickets.isEmpty()) {
            log.info("Daily Digest for vendor {}: No tickets found", vendorId);
            return;
        }

        long openTickets = vendorTickets.stream()
                .filter(t -> t.getStatus() != VendorTicketStatus.VENDOR_RESOLVED
                        && t.getStatus() != VendorTicketStatus.VERIFIED
                        && t.getStatus() != VendorTicketStatus.CLOSED)
                .count();

        long resolvedToday = vendorTickets.stream()
                .filter(t -> t.getResolvedTime() != null
                        && t.getResolvedTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .count();

        long escalatedTickets = vendorTickets.stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.ESCALATED)
                .count();

        Map<VendorTicketStatus, Long> statusBreakdown = vendorTickets.stream()
                .collect(Collectors.groupingBy(VendorTicket::getStatus, Collectors.counting()));

        String ticketSummaries = vendorTickets.stream()
                .map(t -> String.format("  [%s] %s | %s | Priority: %s",
                        t.getStatus(), t.getTicketId(), t.getTitle(), t.getPriority()))
                .collect(Collectors.joining("\n"));

        log.info("========== Daily Digest for Vendor {} ==========", vendorId);
        log.info("Date: {}", LocalDateTime.now().format(FORMATTER));
        log.info("Total tickets: {}", vendorTickets.size());
        log.info("Open tickets: {}", openTickets);
        log.info("Resolved today: {}", resolvedToday);
        log.info("Escalated tickets: {}", escalatedTickets);
        log.info("Status Breakdown: {}", statusBreakdown);
        log.info("Ticket List:");
        log.info(ticketSummaries);
        log.info("========== End of Daily Digest ==========");
    }
}
