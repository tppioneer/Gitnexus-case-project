package com.example.telecom.common.vendor;

import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class VendorSlaReport {

    private final String reportId;
    private final String vendorId;
    private final String vendorName;
    private final LocalDate periodStart;
    private final LocalDate periodEnd;
    private final double slaPercentage;
    private final int totalTickets;
    private final int breachedTickets;
    private final VendorSlaStatus status;
    private final LocalDateTime generatedTime;

    public VendorSlaReport(String reportId, String vendorId, String vendorName,
                           LocalDate periodStart, LocalDate periodEnd,
                           double slaPercentage, int totalTickets,
                           int breachedTickets, VendorSlaStatus status,
                           LocalDateTime generatedTime) {
        this.reportId = reportId;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.slaPercentage = slaPercentage;
        this.totalTickets = totalTickets;
        this.breachedTickets = breachedTickets;
        this.status = status;
        this.generatedTime = generatedTime;
    }

    public String getReportId() { return reportId; }
    public String getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public double getSlaPercentage() { return slaPercentage; }
    public int getTotalTickets() { return totalTickets; }
    public int getBreachedTickets() { return breachedTickets; }
    public VendorSlaStatus getStatus() { return status; }
    public LocalDateTime getGeneratedTime() { return generatedTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorSlaReport that = (VendorSlaReport) o;
        return Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }

    @Override
    public String toString() {
        return "VendorSlaReport{" +
                "reportId='" + reportId + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", slaPercentage=" + slaPercentage +
                ", totalTickets=" + totalTickets +
                ", breachedTickets=" + breachedTickets +
                ", status=" + status +
                ", generatedTime=" + generatedTime +
                '}';
    }
}
