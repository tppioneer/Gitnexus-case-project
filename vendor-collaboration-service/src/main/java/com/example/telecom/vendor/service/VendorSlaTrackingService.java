package com.example.telecom.vendor.service;

import com.example.telecom.vendor.config.VendorCollaborationProperties;
import com.example.telecom.vendor.domain.VendorSlaReport;
import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.repository.VendorSlaRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.common.vendor.VendorSlaStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendorSlaTrackingService {

    private final VendorTicketRepository ticketRepository;
    private final VendorSlaRepository slaRepository;
    private final VendorCollaborationProperties properties;

    public VendorSlaTrackingService(VendorTicketRepository ticketRepository,
                                    VendorSlaRepository slaRepository,
                                    VendorCollaborationProperties properties) {
        this.ticketRepository = ticketRepository;
        this.slaRepository = slaRepository;
        this.properties = properties;
    }

    public void track(String ticketId) {
        ticketRepository.findById(ticketId).ifPresent(ticket -> {
            VendorSlaStatus status = checkSlaBreach(ticketId);
            if (status == VendorSlaStatus.BREACHED) {
                triggerBreachActions(ticket);
            }
        });
    }

    public void updateSlaMetrics(String ticketId) {
        ticketRepository.findById(ticketId).ifPresent(ticket -> {
            VendorSlaStatus status = checkSlaBreach(ticketId);
            if (status == VendorSlaStatus.MET) {
                logSlaMet(ticket);
            } else if (status == VendorSlaStatus.BREACHED) {
                triggerBreachActions(ticket);
            }
        });
    }

    public double calculateSlaPercentage(String vendorId, LocalDate from, LocalDate to) {
        List<VendorTicket> tickets = ticketRepository.findByVendorIdAndDateRange(vendorId, from, to);
        if (tickets.isEmpty()) {
            return 100.0;
        }

        long metCount = tickets.stream()
                .filter(t -> t.getResolvedTime() != null)
                .filter(t -> {
                    Duration resolutionTime = Duration.between(t.getCreatedTime(), t.getResolvedTime());
                    return resolutionTime.toHours() <= properties.getSlaResolutionTimeHours();
                })
                .count();

        return Math.round((double) metCount / tickets.size() * 10000.0) / 100.0;
    }

    public VendorSlaStatus checkSlaBreach(String ticketId) {
        Optional<VendorTicket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            return VendorSlaStatus.PENDING;
        }

        VendorTicket ticket = ticketOpt.get();

        if (ticket.getStatus() == VendorTicketStatus.CLOSED
                || ticket.getStatus() == VendorTicketStatus.VENDOR_RESOLVED
                || ticket.getStatus() == VendorTicketStatus.VERIFIED) {
            if (ticket.getResolvedTime() != null) {
                Duration resolutionTime = Duration.between(ticket.getCreatedTime(), ticket.getResolvedTime());
                if (resolutionTime.toHours() <= properties.getSlaResolutionTimeHours()) {
                    return VendorSlaStatus.MET;
                } else {
                    return VendorSlaStatus.BREACHED;
                }
            }
            return VendorSlaStatus.PENDING;
        }

        Duration elapsed = Duration.between(ticket.getCreatedTime(), LocalDateTime.now());
        if (elapsed.toHours() > properties.getSlaResponseTimeHours()
                && ticket.getStatus() == VendorTicketStatus.AWAITING_VENDOR) {
            return VendorSlaStatus.BREACHED;
        }

        double responseWarningThreshold = properties.getSlaResponseTimeHours() * 0.8;
        if (elapsed.toHours() > responseWarningThreshold
                && ticket.getStatus() == VendorTicketStatus.AWAITING_VENDOR) {
            return VendorSlaStatus.WARNING;
        }

        double resolutionWarningThreshold = properties.getSlaResolutionTimeHours() * 0.8;
        if (ticket.getStatus() == VendorTicketStatus.VENDOR_IN_PROGRESS
                && elapsed.toHours() > resolutionWarningThreshold) {
            return VendorSlaStatus.WARNING;
        }

        if (elapsed.toHours() > properties.getSlaResolutionTimeHours()
                && (ticket.getStatus() == VendorTicketStatus.VENDOR_IN_PROGRESS
                || ticket.getStatus() == VendorTicketStatus.VENDOR_ACKED)) {
            return VendorSlaStatus.BREACHED;
        }

        return VendorSlaStatus.PENDING;
    }

    public VendorSlaReport getVendorSlaReport(String vendorId, LocalDate from, LocalDate to) {
        List<VendorTicket> tickets = ticketRepository.findByVendorIdAndDateRange(vendorId, from, to);
        int totalTickets = tickets.size();

        if (totalTickets == 0) {
            String reportId = UUID.randomUUID().toString();
            VendorSlaReport report = new VendorSlaReport(
                    reportId, vendorId, VendorSlaStatus.MET,
                    100.0, 0, 0, 0, from, to
            );
            return slaRepository.save(report);
        }

        int metCount = 0;
        int breachedCount = 0;
        int warningCount = 0;

        for (VendorTicket ticket : tickets) {
            VendorSlaStatus status = checkSlaBreach(ticket.getTicketId());
            if (status == VendorSlaStatus.MET) {
                metCount++;
            } else if (status == VendorSlaStatus.BREACHED) {
                breachedCount++;
            } else if (status == VendorSlaStatus.WARNING) {
                warningCount++;
            }
        }

        double slaPercentage = totalTickets > 0 ? (double) metCount / totalTickets * 100.0 : 100.0;
        VendorSlaStatus overallStatus;
        if (slaPercentage >= 95.0) {
            overallStatus = VendorSlaStatus.MET;
        } else if (slaPercentage >= 80.0) {
            overallStatus = VendorSlaStatus.WARNING;
        } else {
            overallStatus = VendorSlaStatus.BREACHED;
        }

        String reportId = UUID.randomUUID().toString();
        VendorSlaReport report = new VendorSlaReport(
                reportId, vendorId, overallStatus,
                Math.round(slaPercentage * 100.0) / 100.0,
                metCount, breachedCount, totalTickets, from, to
        );
        return slaRepository.save(report);
    }

    public Map<String, Object> getSlaSummary(String vendorId) {
        List<VendorSlaReport> reports = slaRepository.findByVendorId(vendorId);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("vendorId", vendorId);
        summary.put("totalReports", reports.size());

        if (reports.isEmpty()) {
            summary.put("latestSlaPercentage", 0.0);
            summary.put("latestSlaStatus", VendorSlaStatus.PENDING);
            summary.put("averageSlaPercentage", 0.0);
            return summary;
        }

        Optional<VendorSlaReport> latest = slaRepository.findLatest(vendorId);
        latest.ifPresent(report -> {
            summary.put("latestSlaPercentage", report.getSlaPercentage());
            summary.put("latestSlaStatus", report.getSlaStatus());
            summary.put("latestPeriodFrom", report.getPeriodFrom());
            summary.put("latestPeriodTo", report.getPeriodTo());
        });

        double avgPercentage = reports.stream()
                .mapToDouble(VendorSlaReport::getSlaPercentage)
                .average()
                .orElse(0.0);
        summary.put("averageSlaPercentage", Math.round(avgPercentage * 100.0) / 100.0);

        return summary;
    }

    private void triggerBreachActions(VendorTicket ticket) {
        ticket.incrementEscalationCount();
        ticketRepository.save(ticket);
    }

    private void logSlaMet(VendorTicket ticket) {
        // SLA met - no action required, logging for audit
    }

    private double calculateResponseTime(String ticketId) {
        return ticketRepository.findById(ticketId)
                .filter(t -> t.getAcknowledgedTime() != null)
                .map(t -> Duration.between(t.getCreatedTime(), t.getAcknowledgedTime()).toMinutes() / 60.0)
                .orElse(0.0);
    }
}
