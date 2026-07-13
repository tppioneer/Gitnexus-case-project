package com.example.telecom.sla;

import java.time.LocalDate;

public class SlaContractRequest {

    private String contractName;
    private String vendorId;
    private String regionCode;
    private int responseTimeThreshold;
    private int resolutionTimeThreshold;
    private double availabilityTarget;
    private LocalDate startDate;
    private LocalDate endDate;

    public SlaContractRequest() {
    }

    public SlaContractRequest(String contractName, String vendorId, String regionCode,
                              int responseTimeThreshold, int resolutionTimeThreshold,
                              double availabilityTarget, LocalDate startDate, LocalDate endDate) {
        this.contractName = contractName;
        this.vendorId = vendorId;
        this.regionCode = regionCode;
        this.responseTimeThreshold = responseTimeThreshold;
        this.resolutionTimeThreshold = resolutionTimeThreshold;
        this.availabilityTarget = availabilityTarget;
        this.startDate = startDate;
        this.endDate = endDate;
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
}
