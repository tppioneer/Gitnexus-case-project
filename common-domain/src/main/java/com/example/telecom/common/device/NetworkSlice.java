package com.example.telecom.common.device;

import java.time.LocalDateTime;
import java.util.Objects;

public class NetworkSlice {

    private final String sliceId;
    private final String sliceName;
    private final NetworkSliceStatus status;
    private final String regionCode;
    private final int bandwidth;
    private final int latency;
    private final int maxConnections;
    private final LocalDateTime createdTime;
    private final LocalDateTime modifiedTime;

    public NetworkSlice(String sliceId, String sliceName, NetworkSliceStatus status,
                        String regionCode, int bandwidth, int latency,
                        int maxConnections, LocalDateTime createdTime,
                        LocalDateTime modifiedTime) {
        this.sliceId = sliceId;
        this.sliceName = sliceName;
        this.status = status;
        this.regionCode = regionCode;
        this.bandwidth = bandwidth;
        this.latency = latency;
        this.maxConnections = maxConnections;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public String getSliceId() { return sliceId; }
    public String getSliceName() { return sliceName; }
    public NetworkSliceStatus getStatus() { return status; }
    public String getRegionCode() { return regionCode; }
    public int getBandwidth() { return bandwidth; }
    public int getLatency() { return latency; }
    public int getMaxConnections() { return maxConnections; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public LocalDateTime getModifiedTime() { return modifiedTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkSlice that = (NetworkSlice) o;
        return Objects.equals(sliceId, that.sliceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sliceId);
    }

    @Override
    public String toString() {
        return "NetworkSlice{" +
                "sliceId='" + sliceId + '\'' +
                ", sliceName='" + sliceName + '\'' +
                ", status=" + status +
                ", regionCode='" + regionCode + '\'' +
                ", bandwidth=" + bandwidth +
                ", latency=" + latency +
                ", maxConnections=" + maxConnections +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String sliceId;
        private String sliceName;
        private NetworkSliceStatus status;
        private String regionCode;
        private int bandwidth;
        private int latency;
        private int maxConnections;
        private LocalDateTime createdTime;
        private LocalDateTime modifiedTime;

        private Builder() {}

        public Builder sliceId(String sliceId) { this.sliceId = sliceId; return this; }
        public Builder sliceName(String sliceName) { this.sliceName = sliceName; return this; }
        public Builder status(NetworkSliceStatus status) { this.status = status; return this; }
        public Builder regionCode(String regionCode) { this.regionCode = regionCode; return this; }
        public Builder bandwidth(int bandwidth) { this.bandwidth = bandwidth; return this; }
        public Builder latency(int latency) { this.latency = latency; return this; }
        public Builder maxConnections(int maxConnections) { this.maxConnections = maxConnections; return this; }
        public Builder createdTime(LocalDateTime createdTime) { this.createdTime = createdTime; return this; }
        public Builder modifiedTime(LocalDateTime modifiedTime) { this.modifiedTime = modifiedTime; return this; }

        public NetworkSlice build() {
            return new NetworkSlice(sliceId, sliceName, status, regionCode, bandwidth,
                    latency, maxConnections, createdTime, modifiedTime);
        }
    }
}
