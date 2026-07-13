package com.example.telecom.device.lifecycle.repository;

import com.example.telecom.device.lifecycle.model.DeviceLifecycleState;
import com.example.telecom.device.lifecycle.model.LifecycleRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
public class DeviceLifecycleRepository {

    private final ConcurrentMap<String, LifecycleRecord> store = new ConcurrentHashMap<>();

    public LifecycleRecord save(LifecycleRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("LifecycleRecord must not be null");
        }
        store.put(record.getDeviceId(), record);
        return record;
    }

    public Optional<LifecycleRecord> findById(String deviceId) {
        if (deviceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(deviceId));
    }

    public List<LifecycleRecord> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<LifecycleRecord> findByStatus(DeviceLifecycleState status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> r.getCurrentState() == status)
                .collect(Collectors.toList());
    }

    public List<LifecycleRecord> findByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> {
                    LocalDateTime t = r.getLastTransitionTime();
                    return t != null && !t.isBefore(from) && !t.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    public boolean delete(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        return store.remove(deviceId) != null;
    }

    public long count() {
        return store.size();
    }

    public long countByStatus(DeviceLifecycleState status) {
        if (status == null) {
            return 0;
        }
        return store.values().stream()
                .filter(r -> r.getCurrentState() == status)
                .count();
    }

    public boolean existsById(String deviceId) {
        return deviceId != null && store.containsKey(deviceId);
    }

    public void clear() {
        store.clear();
    }

    public List<LifecycleRecord> findByVendor(String vendor) {
        if (vendor == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> vendor.equals(r.getVendor()))
                .collect(Collectors.toList());
    }

    public List<LifecycleRecord> findByDeviceType(String deviceType) {
        if (deviceType == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> deviceType.equals(r.getDeviceType()))
                .collect(Collectors.toList());
    }

    public List<LifecycleRecord> findByRegionCode(String regionCode) {
        if (regionCode == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(r -> regionCode.equals(r.getRegionCode()))
                .collect(Collectors.toList());
    }
}
