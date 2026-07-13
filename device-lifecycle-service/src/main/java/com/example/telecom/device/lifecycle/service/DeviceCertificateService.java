package com.example.telecom.device.lifecycle.service;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateRequest;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateResponse;
import com.example.telecom.device.lifecycle.model.DeviceCertStatus;
import com.example.telecom.device.lifecycle.model.DeviceCertificate;
import com.example.telecom.device.lifecycle.repository.DeviceCertificateRepository;
import com.example.telecom.device.lifecycle.validator.DeviceLifecycleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceCertificateService {

    private static final Logger log = LoggerFactory.getLogger(DeviceCertificateService.class);

    private final DeviceCertificateRepository certificateRepository;
    private final DeviceLifecycleValidator validator;

    public DeviceCertificateService(DeviceCertificateRepository certificateRepository,
                                     DeviceLifecycleValidator validator) {
        this.certificateRepository = certificateRepository;
        this.validator = validator;
    }

    public DeviceCertificateResponse issue(DeviceCertificateRequest request) {
        validator.validate(request);

        Optional<DeviceCertificate> existing = certificateRepository.findByDeviceId(request.getDeviceId());
        if (existing.isPresent() && existing.get().getStatus() == DeviceCertStatus.ACTIVE) {
            throw new DomainException("CERTIFICATE_ALREADY_EXISTS",
                    "Device " + request.getDeviceId() + " already has an active certificate");
        }

        DeviceCertificate certificate = generateCertificate(request);
        DeviceCertificate saved = certificateRepository.save(certificate);

        log.info("Certificate issued for device {}: {} (expires: {})",
                saved.getDeviceId(), saved.getCertificateId(), saved.getExpiresAt());

        return toResponse(saved);
    }

    public DeviceCertificateResponse renew(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }

        Optional<DeviceCertificate> existingOpt = certificateRepository.findByDeviceId(deviceId);
        if (existingOpt.isEmpty()) {
            throw new DomainException("CERTIFICATE_NOT_FOUND",
                    "No certificate found for device " + deviceId);
        }

        DeviceCertificate existing = existingOpt.get();
        DeviceCertificate renewed = new DeviceCertificate(
                UUID.randomUUID().toString(),
                deviceId,
                generateSerialNumber(),
                existing.getCertificateType(),
                existing.getOrganization(),
                existing.getCommonName(),
                generateFingerprint(deviceId),
                LocalDate.now(),
                LocalDate.now().plusDays(365),
                DeviceCertStatus.ACTIVE
        );

        DeviceCertificate saved = certificateRepository.save(renewed);
        log.info("Certificate renewed for device {}: new certificate {}", deviceId, saved.getCertificateId());

        return toResponse(saved);
    }

    public DeviceCertificateResponse revoke(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }

        Optional<DeviceCertificate> certOpt = certificateRepository.findByDeviceId(deviceId);
        if (certOpt.isEmpty()) {
            throw new DomainException("CERTIFICATE_NOT_FOUND",
                    "No certificate found for device " + deviceId);
        }

        DeviceCertificate cert = certOpt.get();
        Optional<DeviceCertificate> revoked = certificateRepository.revoke(cert.getCertificateId());

        log.info("Certificate revoked for device {}: {}", deviceId, cert.getCertificateId());

        return revoked.map(this::toResponse).orElseThrow(() ->
                new DomainException("REVOKE_FAILED", "Failed to revoke certificate for device " + deviceId));
    }

    public DeviceCertificateResponse getCertificate(String deviceId) {
        if (deviceId == null) {
            throw new DomainException("INVALID_DEVICE_ID", "Device ID must not be null");
        }

        Optional<DeviceCertificate> certOpt = certificateRepository.findByDeviceId(deviceId);
        if (certOpt.isEmpty()) {
            throw new DomainException("CERTIFICATE_NOT_FOUND",
                    "No certificate found for device " + deviceId);
        }

        return toResponse(certOpt.get());
    }

    public List<DeviceCertificate> listExpiring(int days) {
        if (days < 1) {
            throw new DomainException("INVALID_DAYS", "Days must be positive");
        }
        LocalDate threshold = LocalDate.now().plusDays(days);
        List<DeviceCertificate> expiring = certificateRepository.findExpiringBefore(threshold);
        log.info("Found {} certificates expiring within {} days", expiring.size(), days);
        return expiring;
    }

    private DeviceCertificate generateCertificate(DeviceCertificateRequest request) {
        String certificateId = UUID.randomUUID().toString();
        String serialNumber = generateSerialNumber();
        String fingerprint = generateFingerprint(request.getDeviceId());

        DeviceCertificate certificate = new DeviceCertificate(
                certificateId,
                request.getDeviceId(),
                serialNumber,
                request.getCertificateType(),
                request.getOrganization(),
                request.getCommonName(),
                fingerprint,
                LocalDate.now(),
                LocalDate.now().plusDays(request.getValidityDays()),
                DeviceCertStatus.ACTIVE
        );

        validateCertificate(certificate);
        return certificate;
    }

    private void validateCertificate(DeviceCertificate cert) {
        if (cert.getDeviceId() == null || cert.getDeviceId().trim().isEmpty()) {
            throw new DomainException("INVALID_CERTIFICATE", "Certificate device ID is required");
        }
        if (cert.getExpiresAt() != null && cert.getExpiresAt().isBefore(cert.getIssuedAt())) {
            throw new DomainException("INVALID_CERTIFICATE",
                    "Certificate expiry date must be after issue date");
        }
    }

    private String generateSerialNumber() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private String generateFingerprint(String deviceId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((deviceId + UUID.randomUUID()).getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                sb.append(String.format("%02X", hash[i]));
                if (i < hash.length - 1) {
                    sb.append(":");
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Base64.getEncoder().encodeToString((deviceId + System.currentTimeMillis()).getBytes());
        }
    }

    public long countCertificatesByStatus(DeviceCertStatus status) {
        if (status == null) {
            return 0;
        }
        return certificateRepository.countByStatus(status);
    }

    public long getTotalCertificateCount() {
        return certificateRepository.count();
    }

    public boolean hasActiveCertificate(String deviceId) {
        if (deviceId == null) {
            return false;
        }
        Optional<DeviceCertificate> existing = certificateRepository.findByDeviceId(deviceId);
        return existing.isPresent() && existing.get().getStatus() == DeviceCertStatus.ACTIVE;
    }

    public boolean isCertificateExpired(String deviceId) {
        if (deviceId == null) {
            return true;
        }
        Optional<DeviceCertificate> certOpt = certificateRepository.findByDeviceId(deviceId);
        if (certOpt.isEmpty()) {
            return true;
        }
        DeviceCertificate cert = certOpt.get();
        return cert.getExpiresAt() != null && cert.getExpiresAt().isBefore(LocalDate.now());
    }

    private DeviceCertificateResponse toResponse(DeviceCertificate cert) {
        DeviceCertificateResponse response = new DeviceCertificateResponse();
        response.setDeviceId(cert.getDeviceId());
        response.setCertificateId(cert.getCertificateId());
        response.setSerialNumber(cert.getSerialNumber());
        response.setIssuedAt(cert.getIssuedAt());
        response.setExpiresAt(cert.getExpiresAt());
        response.setStatus(cert.getStatus());
        response.setFingerprint(cert.getFingerprint());
        return response;
    }
}
