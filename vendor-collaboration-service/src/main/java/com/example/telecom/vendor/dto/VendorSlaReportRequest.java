package com.example.telecom.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class VendorSlaReportRequest {

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    @NotNull(message = "Period start date is required")
    private LocalDate periodFrom;

    @NotNull(message = "Period end date is required")
    private LocalDate periodTo;

    public VendorSlaReportRequest() {
    }

    public VendorSlaReportRequest(String vendorId, LocalDate periodFrom, LocalDate periodTo) {
        this.vendorId = vendorId;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
    }

    public @NotBlank String getVendorId() { return vendorId; }
    public void setVendorId(@NotBlank String vendorId) { this.vendorId = vendorId; }
    public @NotNull LocalDate getPeriodFrom() { return periodFrom; }
    public void setPeriodFrom(@NotNull LocalDate periodFrom) { this.periodFrom = periodFrom; }
    public @NotNull LocalDate getPeriodTo() { return periodTo; }
    public void setPeriodTo(@NotNull LocalDate periodTo) { this.periodTo = periodTo; }
}
