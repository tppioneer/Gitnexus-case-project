package com.example.telecom.device.lifecycle.mapper;

import com.example.telecom.common.device.DeviceLifecycleStatus;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateResponse;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareResponse;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleEvent;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleRequest;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleResponse;
import com.example.telecom.device.lifecycle.model.AuditEntry;
import com.example.telecom.device.lifecycle.model.DeviceCertificate;
import com.example.telecom.device.lifecycle.model.DeviceFirmwareVersion;
import com.example.telecom.device.lifecycle.model.DeviceLifecycleState;
import com.example.telecom.device.lifecycle.model.LifecycleRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeviceLifecycleMapper {

    public DeviceLifecycleResponse toResponse(DeviceLifecycleStatus status) {
        if (status == null) {
            return null;
        }
        DeviceLifecycleResponse response = new DeviceLifecycleResponse();
        response.setStatus(status);
        response.setTimestamp(LocalDateTime.now());
        response.setMessage("Operation completed");
        return response;
    }

    public DeviceLifecycleResponse toResponse(LifecycleRecord record) {
        if (record == null) {
            return null;
        }
        DeviceLifecycleResponse response = new DeviceLifecycleResponse();
        response.setDeviceId(record.getDeviceId());
        response.setStatus(mapToCommonStatus(record.getCurrentState()));
        if (record.getPreviousState() != null) {
            response.setPreviousStatus(mapToCommonStatus(record.getPreviousState()));
        }
        response.setRegionCode(record.getRegionCode());
        response.setTimestamp(LocalDateTime.now());
        response.setMessage("Operation completed successfully");
        return response;
    }

    public DeviceLifecycleEvent toEvent(String deviceId, DeviceLifecycleState status) {
        if (deviceId == null || status == null) {
            return null;
        }
        return new DeviceLifecycleEvent(deviceId, null, status.name(), null, "system");
    }

    public DeviceLifecycleEvent toEvent(String deviceId, DeviceLifecycleState from, DeviceLifecycleState to, String regionCode, String performedBy) {
        if (deviceId == null) {
            return null;
        }
        return new DeviceLifecycleEvent(deviceId,
                from != null ? from.name() : null,
                to != null ? to.name() : null,
                regionCode, performedBy);
    }

    public DeviceFirmwareResponse toFirmwareResponse(DeviceFirmwareVersion version) {
        if (version == null) {
            return null;
        }
        DeviceFirmwareResponse response = new DeviceFirmwareResponse();
        response.setDeviceId(version.getDeviceId());
        response.setCurrentVersion(version.getVersion());
        response.setTargetVersion(version.getVersion());
        response.setStatus(version.getStatus());
        response.setUpgradeTime(version.getUpgradeTime());
        response.setMessage(version.getMessage());
        return response;
    }

    public DeviceCertificateResponse toCertificateResponse(DeviceCertificate cert) {
        if (cert == null) {
            return null;
        }
        return new DeviceCertificateResponse(
                cert.getDeviceId(),
                cert.getCertificateId(),
                cert.getSerialNumber(),
                cert.getIssuedAt(),
                cert.getExpiresAt(),
                cert.getStatus(),
                cert.getFingerprint()
        );
    }

    public void updateFromRequest(DeviceLifecycleStatus existing, DeviceLifecycleRequest request) {
        if (existing == null || request == null) {
            return;
        }
    }

    public DeviceLifecycleResponse toAuditResponse(AuditEntry entry) {
        if (entry == null) {
            return null;
        }
        DeviceLifecycleResponse response = new DeviceLifecycleResponse();
        response.setDeviceId(entry.getDeviceId());
        response.setTimestamp(entry.getTimestamp());
        response.setMessage("Audit record: " + entry.getAction() + " by " + entry.getPerformedBy());
        return response;
    }

    public void copyFields(DeviceLifecycleRequest source, LifecycleRecord target) {
        if (source == null || target == null) {
            return;
        }
        if (source.getDeviceName() != null) {
            target.setDeviceName(source.getDeviceName());
        }
        if (source.getDeviceType() != null) {
            target.setDeviceType(source.getDeviceType());
        }
        if (source.getVendor() != null) {
            target.setVendor(source.getVendor());
        }
        if (source.getModel() != null) {
            target.setModel(source.getModel());
        }
        if (source.getRegionCode() != null) {
            target.setRegionCode(source.getRegionCode());
        }
        if (source.getIpAddress() != null) {
            target.setIpAddress(source.getIpAddress());
        }
    }

    public static DeviceLifecycleStatus mapToCommonStatus(DeviceLifecycleState state) {
        if (state == null) {
            return null;
        }
        switch (state) {
            case REGISTERED:
            case FIRMWARE_UPGRADING:
            case FAILED:
                return DeviceLifecycleStatus.PROVISIONED;
            case ACTIVE:
                return DeviceLifecycleStatus.ACTIVE;
            case SUSPENDED:
                return DeviceLifecycleStatus.MAINTENANCE;
            case RETIRED:
            case DECOMMISSIONED:
                return DeviceLifecycleStatus.DECOMMISSIONED;
            default:
                return DeviceLifecycleStatus.PROVISIONED;
        }
    }

    public static DeviceLifecycleState mapToInternalState(DeviceLifecycleStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case PROVISIONED:
                return DeviceLifecycleState.REGISTERED;
            case ACTIVE:
                return DeviceLifecycleState.ACTIVE;
            case MAINTENANCE:
                return DeviceLifecycleState.SUSPENDED;
            case FAULTY:
                return DeviceLifecycleState.FAILED;
            case DECOMMISSIONED:
                return DeviceLifecycleState.DECOMMISSIONED;
            default:
                return DeviceLifecycleState.REGISTERED;
        }
    }

    public DeviceLifecycleResponse toResponseWithPrevious(LifecycleRecord record,
                                                           DeviceLifecycleState previousState) {
        if (record == null) {
            return null;
        }
        DeviceLifecycleResponse response = toResponse(record);
        if (previousState != null) {
            response.setPreviousStatus(mapToCommonStatus(previousState));
        }
        return response;
    }

    public List<DeviceLifecycleResponse> toResponseList(List<LifecycleRecord> records) {
        if (records == null) {
            return List.of();
        }
        return records.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<DeviceCertificateResponse> toCertificateResponseList(List<DeviceCertificate> certs) {
        if (certs == null) {
            return List.of();
        }
        return certs.stream()
                .map(this::toCertificateResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}
