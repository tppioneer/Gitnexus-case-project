package com.example.telecom.common.alarm;

import com.example.telecom.common.alarm.FederatedAlarmStatus;
import com.example.telecom.common.alarm.Severity;
import java.time.LocalDateTime;
import java.util.Objects;

public class FederatedAlarmRecord {

    private final String alarmId;
    private final String sourceId;
    private final String sourceRegionCode;
    private final String deviceId;
    private final String alarmType;
    private final Severity severity;
    private final FederatedAlarmStatus status;
    private final LocalDateTime alarmTime;
    private final String description;
    private final String correlationGroupId;
    private final String rawMessage;

    public FederatedAlarmRecord(String alarmId, String sourceId, String sourceRegionCode,
                                String deviceId, String alarmType, Severity severity,
                                FederatedAlarmStatus status, LocalDateTime alarmTime,
                                String description, String correlationGroupId,
                                String rawMessage) {
        this.alarmId = alarmId;
        this.sourceId = sourceId;
        this.sourceRegionCode = sourceRegionCode;
        this.deviceId = deviceId;
        this.alarmType = alarmType;
        this.severity = severity;
        this.status = status;
        this.alarmTime = alarmTime;
        this.description = description;
        this.correlationGroupId = correlationGroupId;
        this.rawMessage = rawMessage;
    }

    public String getAlarmId() { return alarmId; }
    public String getSourceId() { return sourceId; }
    public String getSourceRegionCode() { return sourceRegionCode; }
    public String getDeviceId() { return deviceId; }
    public String getAlarmType() { return alarmType; }
    public Severity getSeverity() { return severity; }
    public FederatedAlarmStatus getStatus() { return status; }
    public LocalDateTime getAlarmTime() { return alarmTime; }
    public String getDescription() { return description; }
    public String getCorrelationGroupId() { return correlationGroupId; }
    public String getRawMessage() { return rawMessage; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FederatedAlarmRecord that = (FederatedAlarmRecord) o;
        return Objects.equals(alarmId, that.alarmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmId);
    }

    @Override
    public String toString() {
        return "FederatedAlarmRecord{" +
                "alarmId='" + alarmId + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sourceRegionCode='" + sourceRegionCode + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", severity=" + severity +
                ", status=" + status +
                ", alarmTime=" + alarmTime +
                ", description='" + description + '\'' +
                ", correlationGroupId='" + correlationGroupId + '\'' +
                ", rawMessage='" + rawMessage + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String alarmId;
        private String sourceId;
        private String sourceRegionCode;
        private String deviceId;
        private String alarmType;
        private Severity severity;
        private FederatedAlarmStatus status;
        private LocalDateTime alarmTime;
        private String description;
        private String correlationGroupId;
        private String rawMessage;

        private Builder() {}

        public Builder alarmId(String alarmId) { this.alarmId = alarmId; return this; }
        public Builder sourceId(String sourceId) { this.sourceId = sourceId; return this; }
        public Builder sourceRegionCode(String sourceRegionCode) { this.sourceRegionCode = sourceRegionCode; return this; }
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder alarmType(String alarmType) { this.alarmType = alarmType; return this; }
        public Builder severity(Severity severity) { this.severity = severity; return this; }
        public Builder status(FederatedAlarmStatus status) { this.status = status; return this; }
        public Builder alarmTime(LocalDateTime alarmTime) { this.alarmTime = alarmTime; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder correlationGroupId(String correlationGroupId) { this.correlationGroupId = correlationGroupId; return this; }
        public Builder rawMessage(String rawMessage) { this.rawMessage = rawMessage; return this; }

        public FederatedAlarmRecord build() {
            return new FederatedAlarmRecord(alarmId, sourceId, sourceRegionCode, deviceId,
                    alarmType, severity, status, alarmTime, description,
                    correlationGroupId, rawMessage);
        }
    }
}
