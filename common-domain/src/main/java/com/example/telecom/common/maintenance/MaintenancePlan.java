package com.example.telecom.common.maintenance;

import com.example.telecom.common.maintenance.MaintenanceStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class MaintenancePlan {

    private final String planId;
    private final String deviceId;
    private final String description;
    private final String regionCode;
    private final LocalDateTime plannedStart;
    private final LocalDateTime plannedEnd;
    private final MaintenanceStatus status;
    private final String createdBy;
    private final LocalDateTime createdTime;
    private final String approvedBy;

    public MaintenancePlan(String planId, String deviceId, String description,
                           String regionCode, LocalDateTime plannedStart,
                           LocalDateTime plannedEnd, MaintenanceStatus status,
                           String createdBy, LocalDateTime createdTime,
                           String approvedBy) {
        this.planId = planId;
        this.deviceId = deviceId;
        this.description = description;
        this.regionCode = regionCode;
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
        this.status = status;
        this.createdBy = createdBy;
        this.createdTime = createdTime;
        this.approvedBy = approvedBy;
    }

    public String getPlanId() { return planId; }
    public String getDeviceId() { return deviceId; }
    public String getDescription() { return description; }
    public String getRegionCode() { return regionCode; }
    public LocalDateTime getPlannedStart() { return plannedStart; }
    public LocalDateTime getPlannedEnd() { return plannedEnd; }
    public MaintenanceStatus getStatus() { return status; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public String getApprovedBy() { return approvedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenancePlan that = (MaintenancePlan) o;
        return Objects.equals(planId, that.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId);
    }

    @Override
    public String toString() {
        return "MaintenancePlan{" +
                "planId='" + planId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", description='" + description + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", plannedStart=" + plannedStart +
                ", plannedEnd=" + plannedEnd +
                ", status=" + status +
                ", createdBy='" + createdBy + '\'' +
                ", createdTime=" + createdTime +
                ", approvedBy='" + approvedBy + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String planId;
        private String deviceId;
        private String description;
        private String regionCode;
        private LocalDateTime plannedStart;
        private LocalDateTime plannedEnd;
        private MaintenanceStatus status;
        private String createdBy;
        private LocalDateTime createdTime;
        private String approvedBy;

        private Builder() {}

        public Builder planId(String planId) { this.planId = planId; return this; }
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }
        public Builder plannedStart(LocalDateTime plannedStart) { this.plannedStart = plannedStart; return this; }
        public Builder plannedEnd(LocalDateTime plannedEnd) { this.plannedEnd = plannedEnd; return this; }
        public Builder status(MaintenanceStatus status) { this.status = status; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdTime(LocalDateTime createdTime) { this.createdTime = createdTime; return this; }
        public Builder approvedBy(String approvedBy) { this.approvedBy = approvedBy; return this; }

        public MaintenancePlan build() {
            return new MaintenancePlan(planId, deviceId, description, regionCode,
                    plannedStart, plannedEnd, status, createdBy, createdTime, approvedBy);
        }
    }
}
