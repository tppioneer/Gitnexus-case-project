package com.example.telecom.device.lifecycle.service;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareRequest;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareResponse;
import com.example.telecom.device.lifecycle.model.DeviceFirmwareVersion;
import com.example.telecom.device.lifecycle.model.FirmwareUpgradeStatus;
import com.example.telecom.device.lifecycle.policy.FirmwareUpgradePolicy;
import com.example.telecom.device.lifecycle.repository.DeviceFirmwareRepository;
import com.example.telecom.device.lifecycle.validator.DeviceLifecycleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceFirmwareService {

    private static final Logger log = LoggerFactory.getLogger(DeviceFirmwareService.class);

    private final DeviceFirmwareRepository firmwareRepository;
    private final FirmwareUpgradePolicy upgradePolicy;
    private final DeviceLifecycleValidator validator;

    public DeviceFirmwareService(DeviceFirmwareRepository firmwareRepository,
                                  FirmwareUpgradePolicy upgradePolicy,
                                  DeviceLifecycleValidator validator) {
        this.firmwareRepository = firmwareRepository;
        this.upgradePolicy = upgradePolicy;
        this.validator = validator;
    }

    public DeviceFirmwareResponse upgrade(DeviceFirmwareRequest request) {
        validator.validate(request);

        String deviceId = request.getDeviceId();
        String targetVersion = request.getTargetVersion();

        Optional<DeviceFirmwareVersion> currentOpt = firmwareRepository.findLatestByDeviceId(deviceId);
        String currentVersion = currentOpt.map(DeviceFirmwareVersion::getVersion).orElse("0.0.0");

        if (!upgradePolicy.isCompatible(currentVersion, targetVersion)) {
            throw new DomainException("INCOMPATIBLE_VERSION",
                    "Cannot upgrade from " + currentVersion + " to " + targetVersion
                            + ": target version must be higher than current version");
        }

        if (!upgradePolicy.isUpgradeAllowed(deviceId, targetVersion)) {
            throw new DomainException("UPGRADE_NOT_ALLOWED",
                    "Firmware upgrade to " + targetVersion + " is not allowed for device " + deviceId);
        }

        DeviceFirmwareVersion firmware = simulateUpgrade(deviceId, targetVersion);

        DeviceFirmwareVersion saved = firmwareRepository.save(firmware);

        log.info("Firmware upgrade for device {} from {} to {} completed successfully",
                deviceId, currentVersion, targetVersion);

        DeviceFirmwareResponse response = new DeviceFirmwareResponse();
        response.setDeviceId(deviceId);
        response.setCurrentVersion(targetVersion);
        response.setTargetVersion(targetVersion);
        response.setStatus(FirmwareUpgradeStatus.COMPLETED);
        response.setUpgradeTime(saved.getUpgradeTime());
        response.setMessage("Firmware upgraded to " + targetVersion);
        return response;
    }

    public String getCurrentVersion(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }
        Optional<DeviceFirmwareVersion> latest = firmwareRepository.findLatestByDeviceId(deviceId);
        return latest.map(DeviceFirmwareVersion::getVersion).orElse("0.0.0");
    }

    public List<DeviceFirmwareVersion> getUpgradeHistory(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }
        List<DeviceFirmwareVersion> history = firmwareRepository.findByDeviceId(deviceId);
        log.info("Retrieved {} firmware upgrade records for device {}", history.size(), deviceId);
        return history;
    }

    public DeviceFirmwareResponse rollback(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }

        Optional<DeviceFirmwareVersion> latestOpt = firmwareRepository.findLatestByDeviceId(deviceId);
        if (latestOpt.isEmpty()) {
            throw new DomainException("NO_FIRMWARE_HISTORY",
                    "No firmware history found for device " + deviceId);
        }

        DeviceFirmwareVersion latest = latestOpt.get();
        String previousVersion = latest.getPreviousVersion();

        if (previousVersion == null || previousVersion.equals("0.0.0")) {
            throw new DomainException("NO_PREVIOUS_VERSION",
                    "No previous firmware version available for rollback on device " + deviceId);
        }

        DeviceFirmwareVersion rollbackVersion = new DeviceFirmwareVersion(
                UUID.randomUUID().toString(),
                deviceId,
                previousVersion,
                latest.getVersion(),
                FirmwareUpgradeStatus.ROLLED_BACK,
                LocalDateTime.now(),
                "Rolled back from " + latest.getVersion()
        );
        firmwareRepository.save(rollbackVersion);

        log.info("Firmware rollback for device {} from {} to {}", deviceId, latest.getVersion(), previousVersion);

        DeviceFirmwareResponse response = new DeviceFirmwareResponse();
        response.setDeviceId(deviceId);
        response.setCurrentVersion(previousVersion);
        response.setTargetVersion(latest.getVersion());
        response.setStatus(FirmwareUpgradeStatus.ROLLED_BACK);
        response.setUpgradeTime(LocalDateTime.now());
        response.setMessage("Rolled back to " + previousVersion);
        return response;
    }

    public boolean checkCompatibility(String deviceId, String firmwareVersion) {
        if (deviceId == null || firmwareVersion == null) {
            return false;
        }
        Optional<DeviceFirmwareVersion> currentOpt = firmwareRepository.findLatestByDeviceId(deviceId);
        String currentVersion = currentOpt.map(DeviceFirmwareVersion::getVersion).orElse("0.0.0");
        return upgradePolicy.isCompatible(currentVersion, firmwareVersion);
    }

    public DeviceFirmwareVersion recordUpgradeAttempt(String deviceId, String targetVersion,
                                                       FirmwareUpgradeStatus status, String message) {
        Optional<DeviceFirmwareVersion> currentOpt = firmwareRepository.findLatestByDeviceId(deviceId);
        String currentVersion = currentOpt.map(DeviceFirmwareVersion::getVersion).orElse("0.0.0");
        DeviceFirmwareVersion attempt = new DeviceFirmwareVersion(
                UUID.randomUUID().toString(), deviceId, targetVersion,
                currentVersion, status, LocalDateTime.now(), message);
        return firmwareRepository.save(attempt);
    }

    public List<DeviceFirmwareVersion> getUpgradeHistoryByStatus(String deviceId, FirmwareUpgradeStatus status) {
        if (deviceId == null || status == null) {
            return List.of();
        }
        return firmwareRepository.findByDeviceId(deviceId).stream()
                .filter(f -> f.getStatus() == status)
                .collect(java.util.stream.Collectors.toList());
    }

    public long countUpgrades(String deviceId) {
        if (deviceId == null) {
            return 0;
        }
        return firmwareRepository.findByDeviceId(deviceId).size();
    }

    public boolean hasUpgradeHistory(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        return !firmwareRepository.findByDeviceId(deviceId).isEmpty();
    }

    private DeviceFirmwareVersion simulateUpgrade(String deviceId, String targetVersion) {
        Optional<DeviceFirmwareVersion> currentOpt = firmwareRepository.findLatestByDeviceId(deviceId);
        String currentVersion = currentOpt.map(DeviceFirmwareVersion::getVersion).orElse("0.0.0");

        DeviceFirmwareVersion firmware = new DeviceFirmwareVersion(
                UUID.randomUUID().toString(),
                deviceId,
                targetVersion,
                currentVersion,
                FirmwareUpgradeStatus.COMPLETED,
                LocalDateTime.now(),
                "Upgrade from " + currentVersion + " to " + targetVersion
        );
        return firmware;
    }
}
