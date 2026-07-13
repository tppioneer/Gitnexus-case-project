package com.example.telecom.sla.service;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.SlaReportRequest;
import com.example.telecom.sla.domain.SlaReport;
import com.example.telecom.sla.event.SlaEventPublisher;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.repository.SlaMetricRepository;
import com.example.telecom.sla.repository.SlaReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlaReportService {

    private static final Logger log = LoggerFactory.getLogger(SlaReportService.class);

    private final SlaReportRepository reportRepository;
    private final SlaContractRepository contractRepository;
    private final SlaMetricRepository metricRepository;
    private final SlaEventPublisher eventPublisher;

    public SlaReportService(SlaReportRepository reportRepository,
                            SlaContractRepository contractRepository,
                            SlaMetricRepository metricRepository,
                            SlaEventPublisher eventPublisher) {
        this.reportRepository = reportRepository;
        this.contractRepository = contractRepository;
        this.metricRepository = metricRepository;
        this.eventPublisher = eventPublisher;
    }

    public SlaReport generateReport(SlaReportRequest request) {
        log.info("Generating SLA report: contractId={}, type={}, period={} to {}",
                request.getContractId(), request.getReportType(),
                request.getPeriodStart(), request.getPeriodEnd());

        SlaContract contract = contractRepository.findById(request.getContractId());
        if (contract == null) {
            log.warn("SLA contract not found for report generation: contractId={}", request.getContractId());
            throw new DomainException("CONTRACT_NOT_FOUND",
                    "SLA contract not found: " + request.getContractId());
        }

        List<SlaMetricSnapshot> metrics;
        if (request.getPeriodStart() != null && request.getPeriodEnd() != null) {
            metrics = metricRepository.findByDateRange(request.getContractId(),
                    request.getPeriodStart().atStartOfDay(),
                    request.getPeriodEnd().atTime(23, 59, 59));
        } else {
            metrics = metricRepository.findByContractId(request.getContractId());
        }

        if (metrics.isEmpty()) {
            log.warn("No metrics found for report generation: contractId={}", request.getContractId());
        }

        Map<String, Double> metricSummary = calculateMetricSummary(metrics);
        Map<String, Double> breachMetrics = calculateBreachMetrics(metrics);
        double slaPercentage = calculateSlaPercentage(metrics);

        Map<String, Double> combinedMetrics = new LinkedHashMap<>();
        combinedMetrics.putAll(metricSummary);
        combinedMetrics.putAll(breachMetrics);

        String reportType = request.getReportType() != null ? request.getReportType() : "PERIODIC";
        String format = request.getFormat() != null ? request.getFormat() : "PDF";
        LocalDate periodStart = request.getPeriodStart() != null ? request.getPeriodStart() : contract.getStartDate();
        LocalDate periodEnd = request.getPeriodEnd() != null ? request.getPeriodEnd() : contract.getEndDate();

        SlaReport report = new SlaReport(
                UUID.randomUUID().toString(),
                request.getContractId(),
                reportType,
                periodStart,
                periodEnd,
                slaPercentage,
                combinedMetrics,
                LocalDateTime.now(),
                "GENERATED",
                format
        );

        SlaReport saved = reportRepository.save(report);
        eventPublisher.publishReportGenerated(saved);
        log.info("SLA report generated successfully: reportId={}, contractId={}, slaPercentage={}",
                saved.getReportId(), request.getContractId(), String.format("%.2f%%", slaPercentage));

        return saved;
    }

    public SlaReport getReport(String reportId) {
        log.debug("Fetching SLA report: reportId={}", reportId);
        SlaReport report = reportRepository.findById(reportId);
        if (report == null) {
            log.warn("SLA report not found: reportId={}", reportId);
            throw new DomainException("REPORT_NOT_FOUND", "SLA report not found: " + reportId);
        }
        return report;
    }

    public List<SlaReport> listReports(String contractId) {
        log.debug("Listing SLA reports: contractId={}", contractId);
        if (contractId != null && !contractId.isBlank()) {
            return reportRepository.findByContractId(contractId);
        }
        return reportRepository.findAll();
    }

    public byte[] exportReport(String reportId, String format) {
        log.info("Exporting SLA report: reportId={}, format={}", reportId, format);
        SlaReport report = reportRepository.findById(reportId);
        if (report == null) {
            throw new DomainException("REPORT_NOT_FOUND", "SLA report not found: " + reportId);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== SLA Report ===\n");
        sb.append("Report ID: ").append(report.getReportId()).append("\n");
        sb.append("Contract ID: ").append(report.getContractId()).append("\n");
        sb.append("Report Type: ").append(report.getReportType()).append("\n");
        sb.append("Period: ").append(report.getPeriodStart()).append(" to ").append(report.getPeriodEnd()).append("\n");
        sb.append("SLA Compliance: ").append(String.format("%.2f%%", report.getSlaPercentage())).append("\n");
        sb.append("Status: ").append(report.getStatus()).append("\n");
        sb.append("Generated: ").append(report.getGeneratedTime()).append("\n");
        sb.append("Format: ").append(format != null ? format : report.getFormat()).append("\n");
        sb.append("\n--- Metrics ---\n");
        if (report.getMetrics() != null && !report.getMetrics().isEmpty()) {
            for (Map.Entry<String, Double> entry : report.getMetrics().entrySet()) {
                sb.append(String.format("  %s: %.2f\n", entry.getKey(), entry.getValue()));
            }
        } else {
            sb.append("  No metrics available\n");
        }
        sb.append("================\n");

        return sb.toString().getBytes();
    }

    public String scheduleReport(String contractId, String cronExpression) {
        log.info("Scheduling report generation: contractId={}, cron={}", contractId, cronExpression);

        if (cronExpression == null || cronExpression.isBlank()) {
            throw new IllegalArgumentException("Cron expression must not be empty");
        }

        String scheduleId = UUID.randomUUID().toString();
        log.info("Report schedule created: scheduleId={}, contractId={}, cron={}",
                scheduleId, contractId, cronExpression);
        return scheduleId;
    }

    public Map<String, Object> getReportSummary() {
        List<SlaReport> allReports = reportRepository.findAll();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalReports", allReports.size());
        summary.put("generated", allReports.stream()
                .filter(r -> "GENERATED".equals(r.getStatus())).count());
        summary.put("scheduled", allReports.stream()
                .filter(r -> "SCHEDULED".equals(r.getStatus())).count());
        summary.put("failed", allReports.stream()
                .filter(r -> "FAILED".equals(r.getStatus())).count());
        summary.put("averageSla", allReports.stream()
                .mapToDouble(SlaReport::getSlaPercentage)
                .average()
                .orElse(100.0));
        return summary;
    }

    private Map<String, Double> calculateMetricSummary(List<SlaMetricSnapshot> metrics) {
        Map<String, Double> summary = new LinkedHashMap<>();
        if (metrics.isEmpty()) {
            return summary;
        }

        Map<String, List<SlaMetricSnapshot>> grouped = metrics.stream()
                .collect(Collectors.groupingBy(SlaMetricSnapshot::getMetricType));

        for (Map.Entry<String, List<SlaMetricSnapshot>> entry : grouped.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(SlaMetricSnapshot::getMetricValue)
                    .average()
                    .orElse(0.0);
            double min = entry.getValue().stream()
                    .mapToDouble(SlaMetricSnapshot::getMetricValue)
                    .min()
                    .orElse(0.0);
            double max = entry.getValue().stream()
                    .mapToDouble(SlaMetricSnapshot::getMetricValue)
                    .max()
                    .orElse(0.0);
            summary.put(entry.getKey() + "_avg", avg);
            summary.put(entry.getKey() + "_min", min);
            summary.put(entry.getKey() + "_max", max);
            summary.put(entry.getKey() + "_count", (double) entry.getValue().size());
        }

        return summary;
    }

    private Map<String, Double> calculateBreachMetrics(List<SlaMetricSnapshot> metrics) {
        Map<String, Double> breachSummary = new LinkedHashMap<>();
        if (metrics.isEmpty()) {
            breachSummary.put("breach_rate", 0.0);
            return breachSummary;
        }

        long breachCount = metrics.stream()
                .filter(m -> m.getMetricValue() > m.getThreshold())
                .count();
        breachSummary.put("breach_rate", (double) breachCount / metrics.size() * 100.0);
        breachSummary.put("total_metrics", (double) metrics.size());
        breachSummary.put("breach_count", (double) breachCount);

        return breachSummary;
    }

    private double calculateSlaPercentage(List<SlaMetricSnapshot> metrics) {
        if (metrics.isEmpty()) {
            return 100.0;
        }

        long metMetrics = metrics.stream()
                .filter(m -> m.getMetricValue() <= m.getThreshold())
                .count();

        return (double) metMetrics / metrics.size() * 100.0;
    }
}
