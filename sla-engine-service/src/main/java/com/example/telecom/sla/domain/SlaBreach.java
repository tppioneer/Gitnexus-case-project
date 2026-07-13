package com.example.telecom.sla.domain;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class SlaBreach {

    private final String breachId;
    private final String contractId;
    private final String metricType;
    private final double actualValue;
    private final double threshold;
    private final Severity severity;
    private VendorSlaStatus status;
    private final LocalDateTime detectedTime;
    private LocalDateTime resolvedTime;
    private EscalationLevel escalatedTo;

    public SlaBreach(String breachId, String contractId, String metricType,
                     double actualValue, double threshold, Severity severity,
                     VendorSlaStatus status, LocalDateTime detectedTime,
                     LocalDateTime resolvedTime, EscalationLevel escalatedTo) {
        this.breachId = breachId;
        this.contractId = contractId;
        this.metricType = metricType;
        this.actualValue = actualValue;
        this.threshold = threshold;
        this.severity = severity;
        this.status = status;
        this.detectedTime = detectedTime;
        this.resolvedTime = resolvedTime;
        this.escalatedTo = escalatedTo;
    }

    public String getBreachId() { return breachId; }
    public String getContractId() { return contractId; }
    public String getMetricType() { return metricType; }
    public double getActualValue() { return actualValue; }
    public double getThreshold() { return threshold; }
    public Severity getSeverity() { return severity; }
    public VendorSlaStatus getStatus() { return status; }
    public LocalDateTime getDetectedTime() { return detectedTime; }
    public LocalDateTime getResolvedTime() { return resolvedTime; }
    public EscalationLevel getEscalatedTo() { return escalatedTo; }

    public void setStatus(VendorSlaStatus status) { this.status = status; }
    public void setResolvedTime(LocalDateTime resolvedTime) { this.resolvedTime = resolvedTime; }
    public void setEscalatedTo(EscalationLevel escalatedTo) { this.escalatedTo = escalatedTo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaBreach slaBreach = (SlaBreach) o;
        return Objects.equals(breachId, slaBreach.breachId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(breachId);
    }

    @Override
    public String toString() {
        return "SlaBreach{" +
                "breachId='" + breachId + '\'' +
                ", contractId='" + contractId + '\'' +
                ", metricType='" + metricType + '\'' +
                ", actualValue=" + actualValue +
                ", threshold=" + threshold +
                ", severity=" + severity +
                ", status=" + status +
                ", detectedTime=" + detectedTime +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String breachId;
        private String contractId;
        private String metricType;
        private double actualValue;
        private double threshold;
        private Severity severity;
        private VendorSlaStatus status;
        private LocalDateTime detectedTime;
        private LocalDateTime resolvedTime;
        private EscalationLevel escalatedTo;

        private Builder() {}

        public Builder breachId(String breachId) { this.breachId = breachId; return this; }
        public Builder contractId(String contractId) { this.contractId = contractId; return this; }
        public Builder metricType(String metricType) { this.metricType = metricType; return this; }
        public Builder actualValue(double actualValue) { this.actualValue = actualValue; return this; }
        public Builder threshold(double threshold) { this.threshold = threshold; return this; }
        public Builder severity(Severity severity) { this.severity = severity; return this; }
        public Builder status(VendorSlaStatus status) { this.status = status; return this; }
        public Builder detectedTime(LocalDateTime detectedTime) { this.detectedTime = detectedTime; return this; }
        public Builder resolvedTime(LocalDateTime resolvedTime) { this.resolvedTime = resolvedTime; return this; }
        public Builder escalatedTo(EscalationLevel escalatedTo) { this.escalatedTo = escalatedTo; return this; }

        public SlaBreach build() {
            return new SlaBreach(breachId, contractId, metricType, actualValue, threshold,
                    severity, status, detectedTime, resolvedTime, escalatedTo);
        }
    }
}
