package com.example.telecom.vendor.service;

import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.dto.VendorPerformanceReport;
import com.example.telecom.vendor.repository.VendorFeedbackRepository;
import com.example.telecom.vendor.repository.VendorPerformanceRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendorPerformanceCalculator {

    private final VendorTicketRepository ticketRepository;
    private final VendorFeedbackRepository feedbackRepository;
    private final VendorPerformanceRepository performanceRepository;

    public VendorPerformanceCalculator(VendorTicketRepository ticketRepository,
                                       VendorFeedbackRepository feedbackRepository,
                                       VendorPerformanceRepository performanceRepository) {
        this.ticketRepository = ticketRepository;
        this.feedbackRepository = feedbackRepository;
        this.performanceRepository = performanceRepository;
    }

    public Map<String, Object> calculate(String vendorId, LocalDate from, LocalDate to) {
        List<VendorTicket> tickets = ticketRepository.findByVendorIdAndDateRange(vendorId, from, to);

        Map<String, Object> performance = new LinkedHashMap<>();
        performance.put("vendorId", vendorId);
        performance.put("periodFrom", from);
        performance.put("periodTo", to);
        performance.put("totalTickets", tickets.size());

        if (tickets.isEmpty()) {
            performance.put("averageResponseTimeHours", 0.0);
            performance.put("responseTimePercentile95", 0.0);
            performance.put("responseTimePercentile50", 0.0);
            performance.put("resolutionRate", 0.0);
            performance.put("qualityScore", 0.0);
            performance.put("overallPerformanceScore", 0.0);
            return performance;
        }

        List<Double> responseTimes = tickets.stream()
                .filter(t -> t.getAcknowledgedTime() != null)
                .map(t -> Duration.between(t.getCreatedTime(), t.getAcknowledgedTime()).toMinutes() / 60.0)
                .collect(Collectors.toList());

        double avgResponseTime = responseTimes.isEmpty() ? 0.0
                : responseTimes.stream().mapToDouble(d -> d).average().orElse(0.0);
        performance.put("averageResponseTimeHours", Math.round(avgResponseTime * 100.0) / 100.0);

        double percentile95 = calculateResponseTimePercentile(vendorId, 95);
        performance.put("responseTimePercentile95", Math.round(percentile95 * 100.0) / 100.0);

        double percentile50 = calculateResponseTimePercentile(vendorId, 50);
        performance.put("responseTimePercentile50", Math.round(percentile50 * 100.0) / 100.0);

        double resolutionRate = calculateResolutionRate(vendorId, from, to);
        performance.put("resolutionRate", Math.round(resolutionRate * 10000.0) / 10000.0);

        double qualityScore = calculateQualityScore(vendorId, from, to);
        performance.put("qualityScore", Math.round(qualityScore * 100.0) / 100.0);

        double avgResponseScore = Math.max(0, 10 - (avgResponseTime / 4.0) * 10);
        double resolutionScore = resolutionRate * 10;
        double overallScore = (avgResponseScore * 0.3 + resolutionScore * 0.4 + qualityScore * 0.3);
        performance.put("overallPerformanceScore", Math.round(overallScore * 100.0) / 100.0);

        long ticketsWithResponse = responseTimes.size();
        long ticketsWithoutResponse = tickets.size() - ticketsWithResponse;
        performance.put("ticketsWithResponse", ticketsWithResponse);
        performance.put("ticketsWithoutResponse", ticketsWithoutResponse);

        if (!responseTimes.isEmpty()) {
            DoubleSummaryStatistics responseStats = responseTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .summaryStatistics();
            performance.put("minResponseTimeHours", Math.round(responseStats.getMin() * 100.0) / 100.0);
            performance.put("maxResponseTimeHours", Math.round(responseStats.getMax() * 100.0) / 100.0);
        }

        Map<String, Long> statusDistribution = tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()));
        performance.put("statusDistribution", statusDistribution);

        return performance;
    }

    public VendorPerformanceReport generatePerformanceReport(String vendorId, LocalDate from, LocalDate to) {
        Map<String, Object> metrics = calculate(vendorId, from, to);

        double avgResponseTime = (Double) metrics.getOrDefault("averageResponseTimeHours", 0.0);
        double percentile95 = (Double) metrics.getOrDefault("responseTimePercentile95", 0.0);
        double resolutionRate = (Double) metrics.getOrDefault("resolutionRate", 0.0);
        double qualityScore = (Double) metrics.getOrDefault("qualityScore", 0.0);
        double overallScore = (Double) metrics.getOrDefault("overallPerformanceScore", 0.0);
        int totalTickets = (Integer) metrics.getOrDefault("totalTickets", 0);

        String reportId = UUID.randomUUID().toString();
        VendorPerformanceReport report = new VendorPerformanceReport(
                reportId, vendorId, avgResponseTime, percentile95,
                resolutionRate, qualityScore, overallScore,
                totalTickets, from, to
        );

        return performanceRepository.save(report);
    }

    public double calculateResponseTimePercentile(String vendorId, int percentile) {
        List<VendorTicket> tickets = ticketRepository.findByVendorId(vendorId);
        List<Double> responseTimes = tickets.stream()
                .filter(t -> t.getAcknowledgedTime() != null)
                .map(t -> Duration.between(t.getCreatedTime(), t.getAcknowledgedTime()).toMinutes() / 60.0)
                .sorted()
                .collect(Collectors.toList());

        if (responseTimes.isEmpty()) {
            return 0.0;
        }

        int index = (int) Math.ceil(percentile / 100.0 * responseTimes.size()) - 1;
        index = Math.max(0, Math.min(index, responseTimes.size() - 1));
        return responseTimes.get(index);
    }

    public double calculateResolutionRate(String vendorId, LocalDate from, LocalDate to) {
        List<VendorTicket> tickets = ticketRepository.findByVendorIdAndDateRange(vendorId, from, to);
        if (tickets.isEmpty()) {
            return 0.0;
        }

        long resolved = tickets.stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.VENDOR_RESOLVED
                        || t.getStatus() == VendorTicketStatus.VERIFIED
                        || t.getStatus() == VendorTicketStatus.CLOSED)
                .count();

        return (double) resolved / tickets.size();
    }

    public double calculateQualityScore(String vendorId, LocalDate from, LocalDate to) {
        List<VendorTicket> tickets = ticketRepository.findByVendorIdAndDateRange(vendorId, from, to);
        if (tickets.isEmpty()) {
            return 0.0;
        }

        return feedbackRepository.getAverageScore(vendorId).orElse(0.0);
    }

    public Map<String, Object> getPerformanceTrend(String vendorId, int lastNMonths) {
        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("vendorId", vendorId);

        List<Map<String, Object>> monthlyData = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = lastNMonths - 1; i >= 0; i--) {
            LocalDate from = now.minusMonths(i + 1).withDayOfMonth(1);
            LocalDate to = now.minusMonths(i).withDayOfMonth(1).minusDays(1);

            if (to.isBefore(from)) {
                to = from.plusMonths(1).minusDays(1);
            }

            Map<String, Object> monthly = calculate(vendorId, from, to);
            monthly.put("month", from.getMonth().toString());
            monthly.put("year", from.getYear());
            monthlyData.add(monthly);
        }

        trend.put("monthlyData", monthlyData);

        if (!monthlyData.isEmpty()) {
            double avgTrend = monthlyData.stream()
                    .mapToDouble(m -> (Double) m.getOrDefault("overallPerformanceScore", 0.0))
                    .average()
                    .orElse(0.0);
            trend.put("averageTrendScore", Math.round(avgTrend * 100.0) / 100.0);
        }

        return trend;
    }
}
