package com.example.telecom.common.sla;

import com.example.telecom.common.vendor.VendorSlaStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class SlaMetricSnapshot {

    private final String snapshotId;
    private final String contractId;
    private final String metricType;
    private final double metricValue;
    private final double threshold;
    private final LocalDateTime timestamp;
    private final VendorSlaStatus status;

    public SlaMetricSnapshot(String snapshotId, String contractId, String metricType,
                             double metricValue, double threshold,
                             LocalDateTime timestamp, VendorSlaStatus status) {
        this.snapshotId = snapshotId;
        this.contractId = contractId;
        this.metricType = metricType;
        this.metricValue = metricValue;
        this.threshold = threshold;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getSnapshotId() { return snapshotId; }
    public String getContractId() { return contractId; }
    public String getMetricType() { return metricType; }
    public double getMetricValue() { return metricValue; }
    public double getThreshold() { return threshold; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public VendorSlaStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaMetricSnapshot that = (SlaMetricSnapshot) o;
        return Objects.equals(snapshotId, that.snapshotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snapshotId);
    }

    @Override
    public String toString() {
        return "SlaMetricSnapshot{" +
                "snapshotId='" + snapshotId + '\'' +
                ", contractId='" + contractId + '\'' +
                ", metricType='" + metricType + '\'' +
                ", metricValue=" + metricValue +
                ", threshold=" + threshold +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }
}
