package com.example.telecom.common.device;

import java.time.LocalDateTime;
import java.util.Objects;

public class PerformanceReport {

    private final String reportId;
    private final String deviceId;
    private final LocalDateTime periodStart;
    private final LocalDateTime periodEnd;
    private final double avgCpuUtilization;
    private final double avgMemoryUtilization;
    private final double avgPacketLoss;
    private final double maxLatency;
    private final double minLatency;
    private final int deviceCount;
    private final LocalDateTime generatedTime;

    public PerformanceReport(String reportId, String deviceId,
                             LocalDateTime periodStart, LocalDateTime periodEnd,
                             double avgCpuUtilization, double avgMemoryUtilization,
                             double avgPacketLoss, double maxLatency,
                             double minLatency, int deviceCount,
                             LocalDateTime generatedTime) {
        this.reportId = reportId;
        this.deviceId = deviceId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.avgCpuUtilization = avgCpuUtilization;
        this.avgMemoryUtilization = avgMemoryUtilization;
        this.avgPacketLoss = avgPacketLoss;
        this.maxLatency = maxLatency;
        this.minLatency = minLatency;
        this.deviceCount = deviceCount;
        this.generatedTime = generatedTime;
    }

    public String getReportId() { return reportId; }
    public String getDeviceId() { return deviceId; }
    public LocalDateTime getPeriodStart() { return periodStart; }
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public double getAvgCpuUtilization() { return avgCpuUtilization; }
    public double getAvgMemoryUtilization() { return avgMemoryUtilization; }
    public double getAvgPacketLoss() { return avgPacketLoss; }
    public double getMaxLatency() { return maxLatency; }
    public double getMinLatency() { return minLatency; }
    public int getDeviceCount() { return deviceCount; }
    public LocalDateTime getGeneratedTime() { return generatedTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceReport that = (PerformanceReport) o;
        return Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }

    @Override
    public String toString() {
        return "PerformanceReport{" +
                "reportId='" + reportId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", avgCpuUtilization=" + avgCpuUtilization +
                ", avgMemoryUtilization=" + avgMemoryUtilization +
                ", avgPacketLoss=" + avgPacketLoss +
                ", maxLatency=" + maxLatency +
                ", minLatency=" + minLatency +
                ", deviceCount=" + deviceCount +
                ", generatedTime=" + generatedTime +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String reportId;
        private String deviceId;
        private LocalDateTime periodStart;
        private LocalDateTime periodEnd;
        private double avgCpuUtilization;
        private double avgMemoryUtilization;
        private double avgPacketLoss;
        private double maxLatency;
        private double minLatency;
        private int deviceCount;
        private LocalDateTime generatedTime;

        private Builder() {}

        public Builder reportId(String reportId) { this.reportId = reportId; return this; }
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder periodStart(LocalDateTime periodStart) { this.periodStart = periodStart; return this; }
        public Builder periodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; return this; }
        public Builder avgCpuUtilization(double avgCpuUtilization) { this.avgCpuUtilization = avgCpuUtilization; return this; }
        public Builder avgMemoryUtilization(double avgMemoryUtilization) { this.avgMemoryUtilization = avgMemoryUtilization; return this; }
        public Builder avgPacketLoss(double avgPacketLoss) { this.avgPacketLoss = avgPacketLoss; return this; }
        public Builder maxLatency(double maxLatency) { this.maxLatency = maxLatency; return this; }
        public Builder minLatency(double minLatency) { this.minLatency = minLatency; return this; }
        public Builder deviceCount(int deviceCount) { this.deviceCount = deviceCount; return this; }
        public Builder generatedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; return this; }

        public PerformanceReport build() {
            return new PerformanceReport(reportId, deviceId, periodStart, periodEnd,
                    avgCpuUtilization, avgMemoryUtilization, avgPacketLoss,
                    maxLatency, minLatency, deviceCount, generatedTime);
        }
    }
}
