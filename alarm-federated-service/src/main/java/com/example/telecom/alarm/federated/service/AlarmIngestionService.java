package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.*;
import com.example.telecom.alarm.federated.event.AlarmFederationEventPublisher;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.repository.FederatedAlarmSourceRepository;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlarmIngestionService {

    private final FederatedAlarmRepository alarmRepository;
    private final FederatedAlarmSourceRepository sourceRepository;
    private final FederatedAlarmSourceRegistry sourceRegistry;
    private final FederatedAlarmDeduplicationService dedupService;
    private final AlarmFederationEventPublisher eventPublisher;

    public AlarmIngestionService(FederatedAlarmRepository alarmRepository,
                                  FederatedAlarmSourceRepository sourceRepository,
                                  FederatedAlarmSourceRegistry sourceRegistry,
                                  FederatedAlarmDeduplicationService dedupService,
                                  AlarmFederationEventPublisher eventPublisher) {
        this.alarmRepository = alarmRepository;
        this.sourceRepository = sourceRepository;
        this.sourceRegistry = sourceRegistry;
        this.dedupService = dedupService;
        this.eventPublisher = eventPublisher;
    }

    public FederatedAlarmRecord ingest(FederatedAlarmRequest request) {
        validateRequest(request);

        String normalizedSourceId = normalizeSource(request.getSourceId());

        if (!validateSource(normalizedSourceId)) {
            throw new ValidationException("sourceId", "Unknown alarm source: " + normalizedSourceId);
        }

        Severity severity = parseSeverity(request.getSeverity());

        FederatedAlarmRecord record = new FederatedAlarmRecord(
                normalizedSourceId,
                request.getDeviceId(),
                request.getAlarmType(),
                severity,
                request.getAlarmTime(),
                request.getDescription(),
                request.getRegionCode(),
                request.getRawMessage()
        );
        record.setAlarmId(UUID.randomUUID().toString());

        enrichAlarm(record);

        if (dedupService.isDuplicate(record)) {
            record.setStatus(FederatedAlarmStatus.DEDUPLICATED);
        }

        FederatedAlarmRecord saved = alarmRepository.save(record);
        eventPublisher.publishAlarmIngested(saved);
        return saved;
    }

    public List<FederatedAlarmRecord> ingestBatch(List<FederatedAlarmRequest> requests) {
        List<FederatedAlarmRecord> ingested = new ArrayList<>();
        for (FederatedAlarmRequest request : requests) {
            try {
                FederatedAlarmRecord record = ingest(request);
                ingested.add(record);
            } catch (Exception e) {
                // Log and continue with remaining items
            }
        }
        return ingested;
    }

    public boolean validateSource(String sourceId) {
        Optional<FederatedAlarmSource> fromRepo = sourceRepository.findById(sourceId);
        Optional<FederatedAlarmSource> fromRegistry = sourceRegistry.resolve(sourceId);
        return fromRepo.isPresent() || fromRegistry.isPresent();
    }

    public FederatedAlarmRecord enrichAlarm(FederatedAlarmRecord record) {
        Optional<FederatedAlarmSource> sourceOpt = sourceRepository.findById(record.getSourceId());
        sourceOpt.ifPresent(source -> {
            if (record.getRegionCode() == null) {
                record.setRegionCode(source.getRegionCode());
            }
        });

        if (record.getDescription() == null || record.getDescription().isBlank()) {
            record.setDescription("Alarm from " + record.getSourceId()
                    + " type: " + record.getAlarmType()
                    + " on device: " + record.getDeviceId());
        }

        return record;
    }

    private void validateRequest(FederatedAlarmRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getSourceId() == null || request.getSourceId().isBlank()) {
            errors.add("Source ID is required");
        }
        if (request.getDeviceId() == null || request.getDeviceId().isBlank()) {
            errors.add("Device ID is required");
        }
        if (request.getAlarmType() == null || request.getAlarmType().isBlank()) {
            errors.add("Alarm type is required");
        }
        if (request.getSeverity() == null || request.getSeverity().isBlank()) {
            errors.add("Severity is required");
        }
        if (request.getAlarmTime() == null) {
            errors.add("Alarm time is required");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("alarm", String.join("; ", errors));
        }
    }

    private Severity parseSeverity(String severityStr) {
        try {
            return Severity.valueOf(severityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("severity", "Invalid severity: " + severityStr);
        }
    }

    private String normalizeSource(String sourceId) {
        if (sourceId == null) {
            return null;
        }
        return sourceId.trim().toLowerCase().replaceAll("\\s+", "_");
    }
}
