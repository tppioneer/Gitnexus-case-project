package com.example.telecom.device.lifecycle.repository;

import com.example.telecom.device.lifecycle.model.DeviceCertStatus;
import com.example.telecom.device.lifecycle.model.DeviceCertificate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
public class DeviceCertificateRepository {

    private final ConcurrentMap<String, DeviceCertificate> store = new ConcurrentHashMap<>();

    public DeviceCertificate save(DeviceCertificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("DeviceCertificate must not be null");
        }
        store.put(certificate.getCertificateId(), certificate);
        return certificate;
    }

    public Optional<DeviceCertificate> findById(String certificateId) {
        if (certificateId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(certificateId));
    }

    public Optional<DeviceCertificate> findByDeviceId(String deviceId) {
        if (deviceId == null) {
            return Optional.empty();
        }
        return store.values().stream()
                .filter(c -> deviceId.equals(c.getDeviceId()))
                .findFirst();
    }

    public List<DeviceCertificate> findExpiringBefore(LocalDate date) {
        if (date == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(c -> c.getExpiresAt() != null
                        && !c.getExpiresAt().isAfter(date)
                        && c.getStatus() == DeviceCertStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public Optional<DeviceCertificate> revoke(String certificateId) {
        if (certificateId == null) {
            return Optional.empty();
        }
        DeviceCertificate cert = store.get(certificateId);
        if (cert != null) {
            cert.setStatus(DeviceCertStatus.REVOKED);
        }
        return Optional.ofNullable(cert);
    }

    public List<DeviceCertificate> findAll() {
        return new ArrayList<>(store.values());
    }

    public long countByStatus(DeviceCertStatus status) {
        if (status == null) {
            return 0;
        }
        return store.values().stream()
                .filter(c -> c.getStatus() == status)
                .count();
    }

    public long count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }

    public List<DeviceCertificate> findByStatus(DeviceCertStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<DeviceCertificate> findByCertificateType(String certificateType) {
        if (certificateType == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(c -> certificateType.equals(c.getCertificateType()))
                .collect(Collectors.toList());
    }

    public Optional<DeviceCertificate> findBySerialNumber(String serialNumber) {
        if (serialNumber == null) {
            return Optional.empty();
        }
        return store.values().stream()
                .filter(c -> serialNumber.equals(c.getSerialNumber()))
                .findFirst();
    }
}
