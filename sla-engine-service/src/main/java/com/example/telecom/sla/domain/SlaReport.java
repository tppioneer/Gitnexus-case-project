package com.example.telecom.sla.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class SlaReport {

    private final String reportId;
    private final String contractId;
    private final String reportType;
    private final LocalDate periodStart;
    private final LocalDate periodEnd;
    private final double slaPercentage;
    private final Map<String, Double> metrics;
    private final LocalDateTime generatedTime;
    private final String status;
    private final String format;

    public SlaReport(String reportId, String contractId, String reportType,
                     LocalDate periodStart, LocalDate periodEnd,
                     double slaPercentage, Map<String, Double> metrics,
                     LocalDateTime generatedTime, String status, String format) {
        this.reportId = reportId;
        this.contractId = contractId;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.slaPercentage = slaPercentage;
        this.metrics = metrics;
        this.generatedTime = generatedTime;
        this.status = status;
        this.format = format;
    }

    public String getReportId() { return reportId; }
    public String getContractId() { return contractId; }
    public String getReportType() { return reportType; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public double getSlaPercentage() { return slaPercentage; }
    public Map<String, Double> getMetrics() { return metrics; }
    public LocalDateTime getGeneratedTime() { return generatedTime; }
    public String getStatus() { return status; }
    public String getFormat() { return format; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaReport slaReport = (SlaReport) o;
        return Objects.equals(reportId, slaReport.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }

    @Override
    public String toString() {
        return "SlaReport{" +
                "reportId='" + reportId + '\'' +
                ", contractId='" + contractId + '\'' +
                ", reportType='" + reportType + '\'' +
                ", slaPercentage=" + slaPercentage +
                ", status='" + status + '\'' +
                '}';
    }
}
