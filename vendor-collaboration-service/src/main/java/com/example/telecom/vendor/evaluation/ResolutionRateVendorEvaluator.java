package com.example.telecom.vendor.evaluation;

import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class ResolutionRateVendorEvaluator implements VendorEvaluator {

    private static final double TARGET_RESOLUTION_RATE = 0.95;
    private static final double MAX_SCORE = 10.0;

    private final VendorTicketRepository ticketRepository;

    public ResolutionRateVendorEvaluator(VendorTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public String getEvaluatorType() {
        return "RESOLUTION_RATE";
    }

    @Override
    public EvaluationResult evaluate(String vendorId) {
        List<VendorTicket> tickets = ticketRepository.findByVendorId(vendorId);

        if (tickets.isEmpty()) {
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("totalTickets", 0);
            metrics.put("resolvedTickets", 0);
            metrics.put("note", "No tickets found for vendor");
            return new EvaluationResult(getEvaluatorType(), 5.0, "No tickets data available", metrics);
        }

        long resolvedTickets = tickets.stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.VENDOR_RESOLVED
                        || t.getStatus() == VendorTicketStatus.VERIFIED
                        || t.getStatus() == VendorTicketStatus.CLOSED)
                .count();

        long escalatedTickets = tickets.stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.ESCALATED)
                .count();

        double resolutionRate = (double) resolvedTickets / tickets.size();
        double score = Math.min(MAX_SCORE, (resolutionRate / TARGET_RESOLUTION_RATE) * MAX_SCORE);
        score = Math.max(0, score);

        List<Long> resolutionTimes = tickets.stream()
                .filter(t -> t.getResolvedTime() != null && t.getCreatedTime() != null)
                .map(t -> Duration.between(t.getCreatedTime(), t.getResolvedTime()).toHours())
                .collect(Collectors.toList());

        double avgResolutionTime = resolutionTimes.isEmpty() ? 0.0
                : resolutionTimes.stream().mapToDouble(d -> (double) d).average().orElse(0.0);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("resolutionRate", Math.round(resolutionRate * 10000.0) / 10000.0);
        metrics.put("resolvedTickets", resolvedTickets);
        metrics.put("totalTickets", tickets.size());
        metrics.put("escalatedTickets", escalatedTickets);
        metrics.put("openTickets", tickets.size() - resolvedTickets);
        metrics.put("averageResolutionTimeHours", Math.round(avgResolutionTime * 100.0) / 100.0);

        String description;
        if (resolutionRate >= TARGET_RESOLUTION_RATE) {
            description = "Good resolution rate: " + String.format("%.2f%%", resolutionRate * 100)
                    + " (meets " + String.format("%.0f%%", TARGET_RESOLUTION_RATE * 100) + " target)";
        } else {
            description = "Below target resolution rate: " + String.format("%.2f%%", resolutionRate * 100)
                    + " (target: " + String.format("%.0f%%", TARGET_RESOLUTION_RATE * 100) + ")";
        }

        return new EvaluationResult(getEvaluatorType(), Math.round(score * 100.0) / 100.0, description, metrics);
    }

    public Map<String, Object> getResolutionMetrics(String vendorId) {
        List<VendorTicket> tickets = ticketRepository.findByVendorId(vendorId);

        long resolved = tickets.stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.VENDOR_RESOLVED
                        || t.getStatus() == VendorTicketStatus.VERIFIED
                        || t.getStatus() == VendorTicketStatus.CLOSED)
                .count();

        long escalated = tickets.stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.ESCALATED)
                .count();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("vendorId", vendorId);
        metrics.put("totalTickets", tickets.size());
        metrics.put("resolvedTickets", resolved);
        metrics.put("escalatedTickets", escalated);
        metrics.put("resolutionRate", tickets.isEmpty() ? 0.0
                : Math.round((double) resolved / tickets.size() * 10000.0) / 10000.0);

        List<Long> resolutionTimes = tickets.stream()
                .filter(t -> t.getResolvedTime() != null && t.getCreatedTime() != null)
                .map(t -> Duration.between(t.getCreatedTime(), t.getResolvedTime()).toHours())
                .collect(Collectors.toList());

        if (!resolutionTimes.isEmpty()) {
            var stats = resolutionTimes.stream()
                    .mapToDouble(d -> (double) d)
                    .summaryStatistics();
            metrics.put("averageResolutionTimeHours", Math.round(stats.getAverage() * 100.0) / 100.0);
            metrics.put("minResolutionTimeHours", Math.round(stats.getMin() * 100.0) / 100.0);
            metrics.put("maxResolutionTimeHours", Math.round(stats.getMax() * 100.0) / 100.0);
        }

        return metrics;
    }
}
