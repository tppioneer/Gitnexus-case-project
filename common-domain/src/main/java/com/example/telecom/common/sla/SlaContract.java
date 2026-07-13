package com.example.telecom.common.sla;

import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class SlaContract {

    private final String contractId;
    private final String contractName;
    private final String vendorId;
    private final String regionCode;
    private final int responseTimeThreshold;
    private final int resolutionTimeThreshold;
    private final double availabilityTarget;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final VendorSlaStatus status;
    private final LocalDateTime createdTime;

    public SlaContract(String contractId, String contractName, String vendorId,
                       String regionCode, int responseTimeThreshold,
                       int resolutionTimeThreshold, double availabilityTarget,
                       LocalDate startDate, LocalDate endDate,
                       VendorSlaStatus status, LocalDateTime createdTime) {
        this.contractId = contractId;
        this.contractName = contractName;
        this.vendorId = vendorId;
        this.regionCode = regionCode;
        this.responseTimeThreshold = responseTimeThreshold;
        this.resolutionTimeThreshold = resolutionTimeThreshold;
        this.availabilityTarget = availabilityTarget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdTime = createdTime;
    }

    public String getContractId() { return contractId; }
    public String getContractName() { return contractName; }
    public String getVendorId() { return vendorId; }
    public String getRegionCode() { return regionCode; }
    public int getResponseTimeThreshold() { return responseTimeThreshold; }
    public int getResolutionTimeThreshold() { return resolutionTimeThreshold; }
    public double getAvailabilityTarget() { return availabilityTarget; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public VendorSlaStatus getStatus() { return status; }
    public LocalDateTime getCreatedTime() { return createdTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaContract that = (SlaContract) o;
        return Objects.equals(contractId, that.contractId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractId);
    }

    @Override
    public String toString() {
        return "SlaContract{" +
                "contractId='" + contractId + '\'' +
                ", contractName='" + contractName + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", responseTimeThreshold=" + responseTimeThreshold +
                ", resolutionTimeThreshold=" + resolutionTimeThreshold +
                ", availabilityTarget=" + availabilityTarget +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", createdTime=" + createdTime +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
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

        private Builder() {}

        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder contractName(String contractName) { this.contractName = contractName; return this; }
        public Builder vendorId(String vendorId) { this.vendorId = vendorId; return this; }
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }
        public Builder responseTimeThreshold(int responseTimeThreshold) { this.responseTimeThreshold = responseTimeThreshold; return this; }
        public Builder resolutionTimeThreshold(int resolutionTimeThreshold) { this.resolutionTimeThreshold = resolutionTimeThreshold; return this; }
        public Builder availabilityTarget(double availabilityTarget) { this.availabilityTarget = availabilityTarget; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder status(VendorSlaStatus status) { this.status = status; return this; }
        public Builder createdTime(LocalDateTime createdTime) { this.createdTime = createdTime; return this; }

        public SlaContract build() {
            return new SlaContract(contractId, contractName, vendorId, regionCode,
                    responseTimeThreshold, resolutionTimeThreshold, availabilityTarget,
                    startDate, endDate, status, createdTime);
        }
    }
}
