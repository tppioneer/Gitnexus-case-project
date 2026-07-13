package com.example.telecom.vendor.evaluation;

import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.domain.VendorTicket;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class ResponseTimeVendorEvaluator implements VendorEvaluator {

    private static final double MAX_EXPECTED_RESPONSE_HOURS = 4.0;
    private static final double MAX_SCORE = 10.0;

    private final VendorTicketRepository ticketRepository;

    public ResponseTimeVendorEvaluator(VendorTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public String getEvaluatorType() {
        return "RESPONSE_TIME";
    }

    @Override
    public EvaluationResult evaluate(String vendorId) {
        List<VendorTicket> tickets = ticketRepository.findByVendorId(vendorId);
        List<Double> responseTimes = tickets.stream()
                .filter(t -> t.getAcknowledgedTime() != null)
                .map(t -> {
                    Duration duration = Duration.between(t.getCreatedTime(), t.getAcknowledgedTime());
                    return duration.toMinutes() / 60.0;
                })
                .collect(Collectors.toList());

        if (responseTimes.isEmpty()) {
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("totalTicketsWithResponse", 0);
            metrics.put("totalTicketsWithoutResponse", tickets.size());
            metrics.put("note", "No acknowledged tickets with response time data");
            return new EvaluationResult(getEvaluatorType(), 5.0, "No response data available", metrics);
        }

        double avgResponseTime = responseTimes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double score = Math.max(0, MAX_SCORE - (avgResponseTime / MAX_EXPECTED_RESPONSE_HOURS) * MAX_SCORE);
        score = Math.min(MAX_SCORE, score);

        List<Double> sortedResponseTimes = new ArrayList<>(responseTimes);
        Collections.sort(sortedResponseTimes);
        double minResponseTime = sortedResponseTimes.get(0);
        double maxResponseTime = sortedResponseTimes.get(sortedResponseTimes.size() - 1);
        double percentile50 = sortedResponseTimes.get(sortedResponseTimes.size() / 2);
        int p95Index = (int) Math.ceil(95.0 / 100.0 * sortedResponseTimes.size()) - 1;
        p95Index = Math.max(0, Math.min(p95Index, sortedResponseTimes.size() - 1));
        double percentile95 = sortedResponseTimes.get(p95Index);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("averageResponseTimeHours", Math.round(avgResponseTime * 100.0) / 100.0);
        metrics.put("minResponseTimeHours", Math.round(minResponseTime * 100.0) / 100.0);
        metrics.put("maxResponseTimeHours", Math.round(maxResponseTime * 100.0) / 100.0);
        metrics.put("medianResponseTimeHours", Math.round(percentile50 * 100.0) / 100.0);
        metrics.put("percentile95ResponseTimeHours", Math.round(percentile95 * 100.0) / 100.0);
        metrics.put("totalTicketsWithResponse", responseTimes.size());
        metrics.put("totalTicketsWithoutResponse", tickets.size() - responseTimes.size());

        String description;
        if (avgResponseTime <= MAX_EXPECTED_RESPONSE_HOURS) {
            description = "Good response time: average " + String.format("%.2f", avgResponseTime)
                    + " hours (within " + (int) MAX_EXPECTED_RESPONSE_HOURS + " hour SLA)";
        } else {
            description = "Slow response time: average " + String.format("%.2f", avgResponseTime)
                    + " hours (exceeds " + (int) MAX_EXPECTED_RESPONSE_HOURS + " hour SLA)";
        }

        return new EvaluationResult(getEvaluatorType(), Math.round(score * 100.0) / 100.0, description, metrics);
    }

    public Map<String, Object> getResponseTimeMetrics(String vendorId) {
        List<VendorTicket> tickets = ticketRepository.findByVendorId(vendorId);
        List<Double> responseTimes = tickets.stream()
                .filter(t -> t.getAcknowledgedTime() != null)
                .map(t -> Duration.between(t.getCreatedTime(), t.getAcknowledgedTime()).toMinutes() / 60.0)
                .collect(Collectors.toList());

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("vendorId", vendorId);
        metrics.put("totalTickets", tickets.size());
        metrics.put("ticketsWithResponse", responseTimes.size());

        if (!responseTimes.isEmpty()) {
            DoubleSummaryStatistics stats = responseTimes.stream()
                    .mapToDouble(d -> d)
                    .summaryStatistics();
            metrics.put("averageResponseTimeHours", Math.round(stats.getAverage() * 100.0) / 100.0);
            metrics.put("minResponseTimeHours", Math.round(stats.getMin() * 100.0) / 100.0);
            metrics.put("maxResponseTimeHours", Math.round(stats.getMax() * 100.0) / 100.0);
        }

        return metrics;
    }
}
