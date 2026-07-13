package com.example.telecom.device.lifecycle.repository;

import com.example.telecom.device.lifecycle.model.DeviceFirmwareVersion;
import com.example.telecom.device.lifecycle.model.FirmwareUpgradeStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
public class DeviceFirmwareRepository {

    private final ConcurrentMap<String, DeviceFirmwareVersion> store = new ConcurrentHashMap<>();

    public DeviceFirmwareVersion save(DeviceFirmwareVersion firmware) {
        if (firmware == null) {
            throw new IllegalArgumentException("DeviceFirmwareVersion must not be null");
        }
        store.put(firmware.getFirmwareId(), firmware);
        return firmware;
    }

    public Optional<DeviceFirmwareVersion> findById(String firmwareId) {
        if (firmwareId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(firmwareId));
    }

    public List<DeviceFirmwareVersion> findByDeviceId(String deviceId) {
        if (deviceId == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(f -> deviceId.equals(f.getDeviceId()))
                .sorted(Comparator.comparing(DeviceFirmwareVersion::getUpgradeTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public Optional<DeviceFirmwareVersion> findLatestByDeviceId(String deviceId) {
        if (deviceId == null) {
            return Optional.empty();
        }
        return store.values().stream()
                .filter(f -> deviceId.equals(f.getDeviceId()))
                .max(Comparator.comparing(DeviceFirmwareVersion::getUpgradeTime,
                        Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public boolean delete(String firmwareId) {
        if (firmwareId == null) {
            return false;
        }
        return store.remove(firmwareId) != null;
    }

    public List<DeviceFirmwareVersion> findAll() {
        return new ArrayList<>(store.values());
    }

    public long count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }

    public List<DeviceFirmwareVersion> findByStatus(FirmwareUpgradeStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(f -> f.getStatus() == status)
                .collect(Collectors.toList());
    }

    public long countByDeviceId(String deviceId) {
        if (deviceId == null) {
            return 0;
        }
        return store.values().stream()
                .filter(f -> deviceId.equals(f.getDeviceId()))
                .count();
    }
}
