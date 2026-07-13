package com.example.telecom.alarm.federated;

import java.time.LocalDateTime;

public class FederatedAlarmSource {

    private String sourceId;
    private String name;
    private String regionCode;
    private String status;
    private String endpoint;
    private LocalDateTime registeredAt;
    private LocalDateTime lastHeartbeat;

    public FederatedAlarmSource() {
        this.registeredAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public FederatedAlarmSource(String sourceId, String name, String regionCode, String endpoint) {
        this();
        this.sourceId = sourceId;
        this.name = name;
        this.regionCode = regionCode;
        this.endpoint = endpoint;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
}
