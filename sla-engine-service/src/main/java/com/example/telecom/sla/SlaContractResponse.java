package com.example.telecom.sla;

import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SlaContractResponse {

    private String contractId;
    private String contractName;
    private String vendorId;
    private String regionCode;
    private int responseTimeThreshold;
    private int resolutionTimeThreshold;
    private double availabilityTarget;
    private LocalDate startDate;
    private LocalDate endDate;
    private VendorSlaStatus status;
    private LocalDateTime createdTime;

    public SlaContractResponse() {
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public int getResponseTimeThreshold() {
        return responseTimeThreshold;
    }

    public void setResponseTimeThreshold(int responseTimeThreshold) {
        this.responseTimeThreshold = responseTimeThreshold;
    }

    public int getResolutionTimeThreshold() {
        return resolutionTimeThreshold;
    }

    public void setResolutionTimeThreshold(int resolutionTimeThreshold) {
        this.resolutionTimeThreshold = resolutionTimeThreshold;
    }

    public double getAvailabilityTarget() {
        return availabilityTarget;
    }

    public void setAvailabilityTarget(double availabilityTarget) {
        this.availabilityTarget = availabilityTarget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public VendorSlaStatus getStatus() {
        return status;
    }

    public void setStatus(VendorSlaStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
