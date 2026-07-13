package com.example.telecom.alarm.federated;

import com.example.telecom.common.alarm.Severity;
import java.time.Duration;
import java.time.LocalDateTime;

public class CrossRegionAlarmView {

    private String alarmId;
    private String sourceRegionCode;
    private String targetRegionCode;
    private String deviceId;
    private Severity severity;
    private FederatedAlarmStatus status;
    private Duration propagationTime;
    private int hops;

    public CrossRegionAlarmView() {
    }

    public CrossRegionAlarmView(String alarmId, String sourceRegionCode, String targetRegionCode,
                                 String deviceId, Severity severity, FederatedAlarmStatus status,
                                 Duration propagationTime, int hops) {
        this.alarmId = alarmId;
        this.sourceRegionCode = sourceRegionCode;
        this.targetRegionCode = targetRegionCode;
        this.deviceId = deviceId;
        this.severity = severity;
        this.status = status;
        this.propagationTime = propagationTime;
        this.hops = hops;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getSourceRegionCode() {
        return sourceRegionCode;
    }

    public void setSourceRegionCode(String sourceRegionCode) {
        this.sourceRegionCode = sourceRegionCode;
    }

    public String getTargetRegionCode() {
        return targetRegionCode;
    }

    public void setTargetRegionCode(String targetRegionCode) {
        this.targetRegionCode = targetRegionCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public FederatedAlarmStatus getStatus() {
        return status;
    }

    public void setStatus(FederatedAlarmStatus status) {
        this.status = status;
    }

    public Duration getPropagationTime() {
        return propagationTime;
    }

    public void setPropagationTime(Duration propagationTime) {
        this.propagationTime = propagationTime;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }
}
