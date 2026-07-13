package com.example.telecom.alarm.federated.mapper;

import com.example.telecom.alarm.federated.*;
import com.example.telecom.alarm.federated.event.FederatedAlarmEvent;
import com.example.telecom.common.alarm.Severity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class FederatedAlarmMapper {

    public FederatedAlarmResponse toResponse(FederatedAlarmRecord record) {
        if (record == null) {
            return null;
        }
        FederatedAlarmResponse response = new FederatedAlarmResponse();
        response.setAlarmId(record.getAlarmId());
        response.setSourceId(record.getSourceId());
        response.setDeviceId(record.getDeviceId());
        response.setAlarmType(record.getAlarmType());
        response.setSeverity(record.getSeverity());
        response.setAlarmTime(record.getAlarmTime());
        response.setRegionCode(record.getRegionCode());
        response.setStatus(record.getStatus());
        response.setCorrelationGroupId(record.getCorrelationGroupId());
        response.setDescription(record.getDescription());
        return response;
    }

    public FederatedAlarmRecord toRecord(FederatedAlarmRequest request) {
        if (request == null) {
            return null;
        }
        Severity severity;
        try {
            severity = Severity.valueOf(request.getSeverity().toUpperCase());
        } catch (IllegalArgumentException e) {
            severity = Severity.INFO;
        }

        FederatedAlarmRecord record = new FederatedAlarmRecord(
                request.getSourceId(),
                request.getDeviceId(),
                request.getAlarmType(),
                severity,
                request.getAlarmTime(),
                request.getDescription(),
                request.getRegionCode(),
                request.getRawMessage()
        );
        return record;
    }

    public FederatedAlarmSummary toSummary(FederatedAlarmRecord record) {
        if (record == null) {
            return null;
        }
        FederatedAlarmSummary summary = new FederatedAlarmSummary();
        summary.setTotalCount(1);
        summary.setLastUpdated(LocalDateTime.now());
        summary.setTimeRange(record.getAlarmTime() != null
                ? record.getAlarmTime().toString() : LocalDateTime.now().toString());
        return summary;
    }

    public CrossRegionAlarmView toCrossRegionView(FederatedAlarmRecord record) {
        if (record == null) {
            return null;
        }
        CrossRegionAlarmView view = new CrossRegionAlarmView();
        view.setAlarmId(record.getAlarmId());
        view.setSourceRegionCode(record.getRegionCode());
        view.setTargetRegionCode(record.getRegionCode());
        view.setDeviceId(record.getDeviceId());
        view.setSeverity(record.getSeverity());
        view.setStatus(record.getStatus());
        view.setPropagationTime(Duration.ZERO);
        view.setHops(0);
        return view;
    }

    public FederatedAlarmRecord updateFromRequest(FederatedAlarmRecord existing, FederatedAlarmRequest request) {
        if (existing == null || request == null) {
            return existing;
        }
        if (request.getSourceId() != null) {
            existing.setSourceId(request.getSourceId());
        }
        if (request.getDeviceId() != null) {
            existing.setDeviceId(request.getDeviceId());
        }
        if (request.getAlarmType() != null) {
            existing.setAlarmType(request.getAlarmType());
        }
        if (request.getSeverity() != null) {
            try {
                existing.setSeverity(Severity.valueOf(request.getSeverity().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // ignore invalid severity
            }
        }
        if (request.getAlarmTime() != null) {
            existing.setAlarmTime(request.getAlarmTime());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getRegionCode() != null) {
            existing.setRegionCode(request.getRegionCode());
        }
        if (request.getRawMessage() != null) {
            existing.setRawMessage(request.getRawMessage());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    public FederatedAlarmEvent toEvent(FederatedAlarmRecord record, String eventType) {
        if (record == null) {
            return null;
        }
        return new FederatedAlarmEvent(
                record.getAlarmId(),
                eventType,
                record.getSourceId(),
                record.getSeverity(),
                record.getRegionCode(),
                record.getAlarmTime()
        );
    }
}
