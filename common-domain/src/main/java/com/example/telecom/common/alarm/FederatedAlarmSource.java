package com.example.telecom.common.alarm;

import java.time.LocalDateTime;
import java.util.Objects;

public class FederatedAlarmSource {

    private final String sourceId;
    private final String sourceName;
    private final String regionCode;
    private final String sourceType;
    private final String status;
    private final LocalDateTime lastHeartbeat;
    private final String endpointUrl;

    public FederatedAlarmSource(String sourceId, String sourceName, String regionCode,
                                String sourceType, String status,
                                LocalDateTime lastHeartbeat, String endpointUrl) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.regionCode = regionCode;
        this.sourceType = sourceType;
        this.status = status;
        this.lastHeartbeat = lastHeartbeat;
        this.endpointUrl = endpointUrl;
    }

    public String getSourceId() { return sourceId; }
    public String getSourceName() { return sourceName; }
    public String getRegionCode() { return regionCode; }
    public String getSourceType() { return sourceType; }
    public String getStatus() { return status; }
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public String getEndpointUrl() { return endpointUrl; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FederatedAlarmSource that = (FederatedAlarmSource) o;
        return Objects.equals(sourceId, that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId);
    }

    @Override
    public String toString() {
        return "FederatedAlarmSource{" +
                "sourceId='" + sourceId + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", status='" + status + '\'' +
                ", lastHeartbeat=" + lastHeartbeat +
                ", endpointUrl='" + endpointUrl + '\'' +
                '}';
    }
}
