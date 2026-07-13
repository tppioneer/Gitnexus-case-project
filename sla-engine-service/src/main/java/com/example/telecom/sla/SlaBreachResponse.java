package com.example.telecom.sla;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDateTime;

public class SlaBreachResponse {

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

    public SlaBreachResponse() {
    }

    public String getBreachId() {
        return breachId;
    }

    public void setBreachId(String breachId) {
        this.breachId = breachId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public double getActualValue() {
        return actualValue;
    }

    public void setActualValue(double actualValue) {
        this.actualValue = actualValue;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public VendorSlaStatus getStatus() {
        return status;
    }

    public void setStatus(VendorSlaStatus status) {
        this.status = status;
    }

    public LocalDateTime getDetectedTime() {
        return detectedTime;
    }

    public void setDetectedTime(LocalDateTime detectedTime) {
        this.detectedTime = detectedTime;
    }

    public LocalDateTime getResolvedTime() {
        return resolvedTime;
    }

    public void setResolvedTime(LocalDateTime resolvedTime) {
        this.resolvedTime = resolvedTime;
    }

    public EscalationLevel getEscalatedTo() {
        return escalatedTo;
    }

    public void setEscalatedTo(EscalationLevel escalatedTo) {
        this.escalatedTo = escalatedTo;
    }
}
