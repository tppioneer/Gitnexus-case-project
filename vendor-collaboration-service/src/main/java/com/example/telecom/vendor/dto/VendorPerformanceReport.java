package com.example.telecom.vendor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VendorPerformanceReport {
    private String reportId;
    private String vendorId;
    private double averageResponseTimeHours;
    private double responseTimePercentile95;
    private double responseTimePercentile50;
    private double resolutionRate;
    private double qualityScore;
    private double overallScore;
    private int ticketsProcessed;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private LocalDateTime generatedTime;

    public VendorPerformanceReport() {
    }

    public VendorPerformanceReport(String reportId, String vendorId, double averageResponseTimeHours,
                                   double responseTimePercentile95, double resolutionRate,
                                   double qualityScore, double overallScore, int ticketsProcessed,
                                   LocalDate periodFrom, LocalDate periodTo) {
        this.reportId = reportId;
        this.vendorId = vendorId;
        this.averageResponseTimeHours = averageResponseTimeHours;
        this.responseTimePercentile95 = responseTimePercentile95;
        this.resolutionRate = resolutionRate;
        this.qualityScore = qualityScore;
        this.overallScore = overallScore;
        this.ticketsProcessed = ticketsProcessed;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.generatedTime = LocalDateTime.now();
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public double getAverageResponseTimeHours() { return averageResponseTimeHours; }
    public void setAverageResponseTimeHours(double averageResponseTimeHours) { this.averageResponseTimeHours = averageResponseTimeHours; }
    public double getResponseTimePercentile95() { return responseTimePercentile95; }
    public void setResponseTimePercentile95(double responseTimePercentile95) { this.responseTimePercentile95 = responseTimePercentile95; }
    public double getResponseTimePercentile50() { return responseTimePercentile50; }
    public void setResponseTimePercentile50(double responseTimePercentile50) { this.responseTimePercentile50 = responseTimePercentile50; }
    public double getResolutionRate() { return resolutionRate; }
    public void setResolutionRate(double resolutionRate) { this.resolutionRate = resolutionRate; }
    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public int getTicketsProcessed() { return ticketsProcessed; }
    public void setTicketsProcessed(int ticketsProcessed) { this.ticketsProcessed = ticketsProcessed; }
    public LocalDate getPeriodFrom() { return periodFrom; }
    public void setPeriodFrom(LocalDate periodFrom) { this.periodFrom = periodFrom; }
    public LocalDate getPeriodTo() { return periodTo; }
    public void setPeriodTo(LocalDate periodTo) { this.periodTo = periodTo; }
    public LocalDateTime getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; }
}
