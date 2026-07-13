package com.example.telecom.vendor.domain;

import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class VendorSlaReport {
    private String reportId;
    private String vendorId;
    private VendorSlaStatus slaStatus;
    private double slaPercentage;
    private int ticketsMet;
    private int ticketsBreached;
    private int ticketsWarning;
    private int totalTickets;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private LocalDateTime generatedTime;

    public VendorSlaReport() {
    }

    public VendorSlaReport(String reportId, String vendorId, VendorSlaStatus slaStatus,
                           double slaPercentage, int ticketsMet, int ticketsBreached,
                           int totalTickets, LocalDate periodFrom, LocalDate periodTo) {
        this.reportId = reportId;
        this.vendorId = vendorId;
        this.slaStatus = slaStatus;
        this.slaPercentage = slaPercentage;
        this.ticketsMet = ticketsMet;
        this.ticketsBreached = ticketsBreached;
        this.totalTickets = totalTickets;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.generatedTime = LocalDateTime.now();
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public VendorSlaStatus getSlaStatus() { return slaStatus; }
    public void setSlaStatus(VendorSlaStatus slaStatus) { this.slaStatus = slaStatus; }
    public double getSlaPercentage() { return slaPercentage; }
    public void setSlaPercentage(double slaPercentage) { this.slaPercentage = slaPercentage; }
    public int getTicketsMet() { return ticketsMet; }
    public void setTicketsMet(int ticketsMet) { this.ticketsMet = ticketsMet; }
    public int getTicketsBreached() { return ticketsBreached; }
    public void setTicketsBreached(int ticketsBreached) { this.ticketsBreached = ticketsBreached; }
    public int getTicketsWarning() { return ticketsWarning; }
    public void setTicketsWarning(int ticketsWarning) { this.ticketsWarning = ticketsWarning; }
    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    public LocalDate getPeriodFrom() { return periodFrom; }
    public void setPeriodFrom(LocalDate periodFrom) { this.periodFrom = periodFrom; }
    public LocalDate getPeriodTo() { return periodTo; }
    public void setPeriodTo(LocalDate periodTo) { this.periodTo = periodTo; }
    public LocalDateTime getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; }

    public double getSlaComplianceRate() {
        if (totalTickets == 0) {
            return 100.0;
        }
        return (double) ticketsMet / totalTickets * 100.0;
    }

    public boolean hasBreaches() {
        return ticketsBreached > 0;
    }

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
                ", slaStatus=" + slaStatus +
                ", slaPercentage=" + slaPercentage +
                '}';
    }
}
