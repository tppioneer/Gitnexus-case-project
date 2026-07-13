package com.example.telecom.device.lifecycle.service;

import com.example.telecom.common.device.DeviceLifecycleStatus;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleRequest;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleResponse;
import com.example.telecom.device.lifecycle.event.DeviceLifecycleEventPublisher;
import com.example.telecom.device.lifecycle.mapper.DeviceLifecycleMapper;
import com.example.telecom.device.lifecycle.model.AuditEntry;
import com.example.telecom.device.lifecycle.model.DeviceLifecycleState;
import com.example.telecom.device.lifecycle.model.LifecycleRecord;
import com.example.telecom.device.lifecycle.repository.DeviceLifecycleRepository;
import com.example.telecom.device.lifecycle.validator.DeviceLifecycleValidator;
import com.example.telecom.device.lifecycle.workflow.DeviceStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(DeviceLifecycleService.class);

    private final DeviceLifecycleRepository repository;
    private final DeviceStateMachine stateMachine;
    private final DeviceLifecycleValidator validator;
    private final DeviceLifecycleEventPublisher eventPublisher;
    private final DeviceLifecycleMapper mapper;
    private final DeviceLifecycleAuditService auditService;

    public DeviceLifecycleService(DeviceLifecycleRepository repository,
                                   DeviceStateMachine stateMachine,
                                   DeviceLifecycleValidator validator,
                                   DeviceLifecycleEventPublisher eventPublisher,
                                   DeviceLifecycleMapper mapper,
                                   DeviceLifecycleAuditService auditService) {
        this.repository = repository;
        this.stateMachine = stateMachine;
        this.validator = validator;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    public DeviceLifecycleResponse register(DeviceLifecycleRequest request) {
        validator.validate(request);

        if (repository.existsById(request.getDeviceId())) {
            throw new DomainException("DEVICE_ALREADY_EXISTS",
                    "Device " + request.getDeviceId() + " is already registered");
        }

        LifecycleRecord record = new LifecycleRecord(
                request.getDeviceId(),
                request.getDeviceName(),
                request.getDeviceType(),
                request.getVendor(),
                request.getModel(),
                request.getRegionCode(),
                request.getIpAddress(),
                DeviceLifecycleState.REGISTERED
        );

        repository.save(record);
        eventPublisher.publishRegistered(request.getDeviceId());
        auditService.recordAction(request.getDeviceId(), "REGISTER", "system");

        log.info("Device {} registered successfully", request.getDeviceId());
        return mapper.toResponse(record);
    }

    public DeviceLifecycleResponse activate(String deviceId) {
        validator.validateDeviceId(deviceId);

        LifecycleRecord record = findRecordOrThrow(deviceId);
        DeviceLifecycleState currentState = record.getCurrentState();
        DeviceLifecycleState targetState = DeviceLifecycleState.ACTIVE;

        validateTransition(currentState, targetState);

        record.setPreviousState(currentState);
        record.setCurrentState(targetState);
        record.setLastTransitionTime(LocalDateTime.now());
        repository.save(record);

        eventPublisher.publishActivated(deviceId);
        auditService.recordAction(deviceId, "ACTIVATE", "system");

        log.info("Device {} activated successfully", deviceId);
        return mapper.toResponse(record);
    }

    public DeviceLifecycleResponse suspend(String deviceId) {
        validator.validateDeviceId(deviceId);

        LifecycleRecord record = findRecordOrThrow(deviceId);
        DeviceLifecycleState currentState = record.getCurrentState();
        DeviceLifecycleState targetState = DeviceLifecycleState.SUSPENDED;

        validateTransition(currentState, targetState);

        record.setPreviousState(currentState);
        record.setCurrentState(targetState);
        record.setLastTransitionTime(LocalDateTime.now());
        repository.save(record);

        eventPublisher.publishSuspended(deviceId);
        auditService.recordAction(deviceId, "SUSPEND", "system");

        log.info("Device {} suspended successfully", deviceId);
        return mapper.toResponse(record);
    }

    public DeviceLifecycleResponse retire(String deviceId) {
        validator.validateDeviceId(deviceId);

        LifecycleRecord record = findRecordOrThrow(deviceId);
        DeviceLifecycleState currentState = record.getCurrentState();
        DeviceLifecycleState targetState = DeviceLifecycleState.RETIRED;

        validateTransition(currentState, targetState);

        record.setPreviousState(currentState);
        record.setCurrentState(targetState);
        record.setLastTransitionTime(LocalDateTime.now());
        repository.save(record);

        eventPublisher.publishRetired(deviceId);
        auditService.recordAction(deviceId, "RETIRE", "system");

        log.info("Device {} retired successfully", deviceId);
        return mapper.toResponse(record);
    }

    public DeviceLifecycleResponse decommission(String deviceId) {
        validator.validateDeviceId(deviceId);

        LifecycleRecord record = findRecordOrThrow(deviceId);
        DeviceLifecycleState currentState = record.getCurrentState();
        DeviceLifecycleState targetState = DeviceLifecycleState.DECOMMISSIONED;

        validateTransition(currentState, targetState);

        record.setPreviousState(currentState);
        record.setCurrentState(targetState);
        record.setLastTransitionTime(LocalDateTime.now());
        repository.save(record);

        eventPublisher.publishDecommissioned(deviceId);
        auditService.recordAction(deviceId, "DECOMMISSION", "system");

        log.info("Device {} decommissioned successfully", deviceId);
        return mapper.toResponse(record);
    }

    public DeviceLifecycleResponse getStatus(String deviceId) {
        validator.validateDeviceId(deviceId);

        LifecycleRecord record = findRecordOrThrow(deviceId);
        return mapper.toResponse(record);
    }

    public List<DeviceLifecycleResponse> listByStatus(DeviceLifecycleStatus status) {
        if (status == null) {
            return List.of();
        }
        DeviceLifecycleState internalState = DeviceLifecycleMapper.mapToInternalState(status);
        List<LifecycleRecord> records = repository.findByStatus(internalState);
        return records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getLifecycleHistory(String deviceId) {
        validator.validateDeviceId(deviceId);

        if (!repository.existsById(deviceId)) {
            throw new DomainException("DEVICE_NOT_FOUND", "Device " + deviceId + " not found");
        }

        return auditService.getHistory(deviceId);
    }

    public DeviceLifecycleResponse getStatusByDeviceIdAndDateRange(String deviceId,
                                                                    LocalDateTime from, LocalDateTime to) {
        validator.validateDeviceId(deviceId);
        if (!repository.existsById(deviceId)) {
            throw new DomainException("DEVICE_NOT_FOUND", "Device " + deviceId + " not found");
        }
        LifecycleRecord record = findRecordOrThrow(deviceId);
        DeviceLifecycleResponse response = mapper.toResponse(record);
        List<AuditEntry> history = auditService.getHistoryByDateRange(deviceId, from, to);
        response.setMessage("Device found with " + history.size() + " audit entries in date range");
        return response;
    }

    public long countDevicesByStatus(DeviceLifecycleStatus status) {
        if (status == null) {
            return 0;
        }
        DeviceLifecycleState internalState = DeviceLifecycleMapper.mapToInternalState(status);
        return repository.countByStatus(internalState);
    }

    public long getTotalDeviceCount() {
        return repository.count();
    }

    public DeviceLifecycleResponse updateDeviceInfo(String deviceId, DeviceLifecycleRequest request) {
        validator.validateDeviceId(deviceId);
        LifecycleRecord record = findRecordOrThrow(deviceId);
        mapper.copyFields(request, record);
        record.setLastTransitionTime(LocalDateTime.now());
        repository.save(record);
        auditService.recordAction(deviceId, "UPDATE_INFO", "system");
        log.info("Device {} info updated successfully", deviceId);
        return mapper.toResponse(record);
    }

    public boolean isDeviceRegistered(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        return repository.existsById(deviceId);
    }

    public List<DeviceLifecycleResponse> listAllDevices() {
        List<LifecycleRecord> records = repository.findAll();
        return records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<DeviceLifecycleResponse> listByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return List.of();
        }
        List<LifecycleRecord> records = repository.findByDateRange(from, to);
        return records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteDevice(String deviceId) {
        validator.validateDeviceId(deviceId);
        if (!repository.existsById(deviceId)) {
            throw new DomainException("DEVICE_NOT_FOUND", "Device " + deviceId + " not found");
        }
        repository.delete(deviceId);
        auditService.recordAction(deviceId, "DELETE", "system");
        log.info("Device {} deleted successfully", deviceId);
    }

    private LifecycleRecord findRecordOrThrow(String deviceId) {
        Optional<LifecycleRecord> optional = repository.findById(deviceId);
        if (optional.isEmpty()) {
            throw new DomainException("DEVICE_NOT_FOUND",
                    "Device " + deviceId + " not found");
        }
        return optional.get();
    }

    private void validateTransition(DeviceLifecycleState from, DeviceLifecycleState to) {
        if (!stateMachine.isValidTransition(from, to)) {
            throw new DomainException("INVALID_TRANSITION",
                    "Cannot transition device from " + from + " to " + to);
        }
    }
}
