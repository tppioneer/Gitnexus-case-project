package com.example.telecom.sla;

import java.time.LocalDate;

public class SlaReportRequest {

    private String contractId;
    private String reportType;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String format;

    public SlaReportRequest() {
    }

    public SlaReportRequest(String contractId, String reportType,
                            LocalDate periodStart, LocalDate periodEnd, String format) {
        this.contractId = contractId;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.format = format;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
