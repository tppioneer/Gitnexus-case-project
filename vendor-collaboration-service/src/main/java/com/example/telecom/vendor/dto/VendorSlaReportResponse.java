package com.example.telecom.vendor.dto;

import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VendorSlaReportResponse {
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

    public VendorSlaReportResponse() {
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
}
