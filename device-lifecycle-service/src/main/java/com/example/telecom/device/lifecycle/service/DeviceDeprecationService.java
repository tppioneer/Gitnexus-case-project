package com.example.telecom.device.lifecycle.service;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceLifecycleStatus;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleResponse;
import com.example.telecom.device.lifecycle.mapper.DeviceLifecycleMapper;
import com.example.telecom.device.lifecycle.model.DeviceLifecycleState;
import com.example.telecom.device.lifecycle.model.LifecycleRecord;
import com.example.telecom.device.lifecycle.policy.DeviceDeprecationPolicy;
import com.example.telecom.device.lifecycle.repository.DeviceLifecycleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class DeviceDeprecationService {

    private static final Logger log = LoggerFactory.getLogger(DeviceDeprecationService.class);

    private final DeviceDeprecationPolicy deprecationPolicy;
    private final DeviceLifecycleRepository lifecycleRepository;
    private final DeviceLifecycleMapper mapper;
    private final ConcurrentMap<String, LocalDate> scheduledDeprecations = new ConcurrentHashMap<>();

    public DeviceDeprecationService(DeviceDeprecationPolicy deprecationPolicy,
                                     DeviceLifecycleRepository lifecycleRepository,
                                     DeviceLifecycleMapper mapper) {
        this.deprecationPolicy = deprecationPolicy;
        this.lifecycleRepository = lifecycleRepository;
        this.mapper = mapper;
    }

    public DeviceLifecycleResponse evaluateDeprecation(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }

        Optional<LifecycleRecord> recordOpt = lifecycleRepository.findById(deviceId);
        if (recordOpt.isEmpty()) {
            throw new DomainException("DEVICE_NOT_FOUND", "Device " + deviceId + " not found");
        }

        LifecycleRecord record = recordOpt.get();
        DeviceInfo deviceInfo = toDeviceInfo(record);

        boolean eligible = deprecationPolicy.evaluate(deviceInfo);
        if (!eligible) {
            DeviceLifecycleResponse response = mapper.toResponse(record);
            response.setMessage("Device " + deviceId + " is not eligible for deprecation");
            return response;
        }

        String reason = deprecationPolicy.getDeprecationReason(deviceInfo);
        log.info("Device {} evaluated for deprecation: eligible={}, reason={}", deviceId, eligible, reason);

        DeviceLifecycleResponse response = mapper.toResponse(record);
        response.setMessage(reason);
        return response;
    }

    public void scheduleDeprecation(String deviceId, LocalDate deprecationDate) {
        if (deviceId == null || deprecationDate == null) {
            throw new DomainException("INVALID_PARAMETERS", "Device ID and deprecation date are required");
        }

        Optional<LifecycleRecord> recordOpt = lifecycleRepository.findById(deviceId);
        if (recordOpt.isEmpty()) {
            throw new DomainException("DEVICE_NOT_FOUND", "Device " + deviceId + " not found");
        }

        if (deprecationDate.isBefore(LocalDate.now())) {
            throw new DomainException("INVALID_DATE", "Deprecation date must be in the future");
        }

        scheduledDeprecations.put(deviceId, deprecationDate);
        log.info("Deprecation scheduled for device {} on {}", deviceId, deprecationDate);
    }

    public void cancelDeprecation(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }

        LocalDate removed = scheduledDeprecations.remove(deviceId);
        if (removed == null) {
            throw new DomainException("NO_SCHEDULED_DEPRECATION",
                    "No scheduled deprecation found for device " + deviceId);
        }

        log.info("Deprecation cancelled for device {}", deviceId);
    }

    public int getDeprecationPolicy(String deviceType) {
        if (deviceType == null) {
            return deprecationPolicy.getMinAgeYears();
        }
        try {
            DeviceType type = DeviceType.valueOf(deviceType);
            return deprecationPolicy.getDeviceTypeAgePolicy(type);
        } catch (IllegalArgumentException e) {
            return deprecationPolicy.getMinAgeYears();
        }
    }

    public List<String> listPendingDeprecation() {
        List<String> pendingDevices = new ArrayList<>(scheduledDeprecations.keySet());
        log.info("Found {} devices pending deprecation", pendingDevices.size());
        return pendingDevices;
    }

    private double calculateDeviceAge(DeviceInfo device) {
        return 0;
    }

    public boolean isDeprecationScheduled(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        return scheduledDeprecations.containsKey(deviceId);
    }

    public LocalDate getScheduledDeprecationDate(String deviceId) {
        if (deviceId == null) {
            return null;
        }
        return scheduledDeprecations.get(deviceId);
    }

    public List<String> listDeprecatedDevices() {
        return lifecycleRepository.findAll().stream()
                .filter(r -> r.getCurrentState() == DeviceLifecycleState.DECOMMISSIONED)
                .map(LifecycleRecord::getDeviceId)
                .collect(Collectors.toList());
    }

    public long countPendingDeprecations() {
        return scheduledDeprecations.size();
    }

    private DeviceInfo toDeviceInfo(LifecycleRecord record) {
        DeviceInfo info = new DeviceInfo();
        info.setDeviceId(record.getDeviceId());
        info.setDeviceName(record.getDeviceName());
        try {
            info.setDeviceType(DeviceType.valueOf(record.getDeviceType()));
        } catch (IllegalArgumentException e) {
            info.setDeviceType(DeviceType.ROUTER);
        }
        info.setVendor(record.getVendor());
        info.setMaintenanceRegionCode(record.getRegionCode());
        info.setActive(record.getCurrentState() == DeviceLifecycleState.ACTIVE);
        return info;
    }
}
