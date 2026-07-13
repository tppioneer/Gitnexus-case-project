package com.example.telecom.device.lifecycle;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateRequest;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateResponse;
import com.example.telecom.device.lifecycle.model.DeviceCertStatus;
import com.example.telecom.device.lifecycle.model.DeviceCertificate;
import com.example.telecom.device.lifecycle.repository.DeviceCertificateRepository;
import com.example.telecom.device.lifecycle.service.DeviceCertificateService;
import com.example.telecom.device.lifecycle.validator.DeviceLifecycleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceCertificateServiceTest {

    @Mock
    private DeviceCertificateRepository certificateRepository;

    private DeviceLifecycleValidator validator;
    private DeviceCertificateService certificateService;

    @BeforeEach
    void setUp() {
        validator = new DeviceLifecycleValidator();
        certificateService = new DeviceCertificateService(certificateRepository, validator);
    }

    @Test
    void testIssueCertificate() {
        String deviceId = "DEV-TEST001";
        DeviceCertificateRequest request = new DeviceCertificateRequest(
                deviceId, "TLS", 365, "Telecom Corp", "device001.telecom.com");

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());
        when(certificateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DeviceCertificateResponse response = certificateService.issue(request);

        assertNotNull(response);
        assertEquals(deviceId, response.getDeviceId());
        assertNotNull(response.getCertificateId());
        assertNotNull(response.getSerialNumber());
        assertNotNull(response.getFingerprint());
        assertEquals(DeviceCertStatus.ACTIVE, response.getStatus());
        verify(certificateRepository).save(any());
    }

    @Test
    void testRevokeCertificate() {
        String deviceId = "DEV-TEST002";
        String certId = UUID.randomUUID().toString();
        DeviceCertificate existing = new DeviceCertificate(
                certId, deviceId, "SN12345", "TLS", "Telecom Corp",
                "device002.telecom.com", "AA:BB:CC:DD",
                LocalDate.now().minusDays(100), LocalDate.now().plusDays(265),
                DeviceCertStatus.ACTIVE);

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(existing));
        when(certificateRepository.revoke(certId)).thenReturn(Optional.of(existing));

        DeviceCertificateResponse response = certificateService.revoke(deviceId);

        assertNotNull(response);
        assertEquals(deviceId, response.getDeviceId());
        verify(certificateRepository).revoke(certId);
    }

    @Test
    void testListExpiringCertificates() {
        LocalDate expiryDate = LocalDate.now().plusDays(15);
        DeviceCertificate cert1 = new DeviceCertificate(
                UUID.randomUUID().toString(), "DEV-EXP001", "SN001", "TLS",
                "Org", "dev1", "FP1",
                LocalDate.now(), expiryDate, DeviceCertStatus.ACTIVE);
        DeviceCertificate cert2 = new DeviceCertificate(
                UUID.randomUUID().toString(), "DEV-EXP002", "SN002", "TLS",
                "Org", "dev2", "FP2",
                LocalDate.now(), expiryDate.minusDays(5), DeviceCertStatus.ACTIVE);

        when(certificateRepository.findExpiringBefore(any(LocalDate.class)))
                .thenReturn(List.of(cert1, cert2));

        List<DeviceCertificate> expiring = certificateService.listExpiring(30);

        assertNotNull(expiring);
        assertEquals(2, expiring.size());
        verify(certificateRepository).findExpiringBefore(any(LocalDate.class));
    }

    @Test
    void testIssueCertificateAlreadyExists() {
        String deviceId = "DEV-TEST003";
        DeviceCertificateRequest request = new DeviceCertificateRequest(
                deviceId, "TLS", 365, "Telecom Corp", "device003.telecom.com");

        DeviceCertificate existing = new DeviceCertificate(
                UUID.randomUUID().toString(), deviceId, "SN003", "TLS",
                "Telecom Corp", "device003.telecom.com", "FP003",
                LocalDate.now(), LocalDate.now().plusDays(365), DeviceCertStatus.ACTIVE);

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(existing));

        assertThrows(DomainException.class, () -> certificateService.issue(request));
    }

    @Test
    void testRenewCertificate() {
        String deviceId = "DEV-TEST004";
        DeviceCertificate existing = new DeviceCertificate(
                UUID.randomUUID().toString(), deviceId, "SN004", "TLS",
                "Telecom Corp", "device004.telecom.com", "FP004",
                LocalDate.now().minusDays(300), LocalDate.now().plusDays(65),
                DeviceCertStatus.ACTIVE);

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(existing));
        when(certificateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DeviceCertificateResponse response = certificateService.renew(deviceId);

        assertNotNull(response);
        assertEquals(deviceId, response.getDeviceId());
        assertNotNull(response.getCertificateId());
        verify(certificateRepository, times(1)).save(any());
    }

    @Test
    void testRenewCertificateNotFound() {
        when(certificateRepository.findByDeviceId("DEV-NOTFOUND")).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> certificateService.renew("DEV-NOTFOUND"));
    }

    @Test
    void testRevokeCertificateNotFound() {
        when(certificateRepository.findByDeviceId("DEV-NOTFOUND")).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> certificateService.revoke("DEV-NOTFOUND"));
    }

    @Test
    void testGetCertificate() {
        String deviceId = "DEV-TEST005";
        DeviceCertificate existing = new DeviceCertificate(
                UUID.randomUUID().toString(), deviceId, "SN005", "TLS",
                "Org", "dev5", "FP005",
                LocalDate.now(), LocalDate.now().plusDays(365), DeviceCertStatus.ACTIVE);

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(existing));

        DeviceCertificateResponse response = certificateService.getCertificate(deviceId);
        assertNotNull(response);
        assertEquals(deviceId, response.getDeviceId());
    }

    @Test
    void testHasActiveCertificate() {
        String deviceId = "DEV-TEST006";
        DeviceCertificate active = new DeviceCertificate(
                UUID.randomUUID().toString(), deviceId, "SN006", "TLS",
                "Org", "dev6", "FP006",
                LocalDate.now(), LocalDate.now().plusDays(365), DeviceCertStatus.ACTIVE);

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(active));

        assertTrue(certificateService.hasActiveCertificate(deviceId));
    }

    @Test
    void testIsCertificateExpired() {
        String deviceId = "DEV-TEST007";
        DeviceCertificate expired = new DeviceCertificate(
                UUID.randomUUID().toString(), deviceId, "SN007", "TLS",
                "Org", "dev7", "FP007",
                LocalDate.now().minusDays(400), LocalDate.now().minusDays(35),
                DeviceCertStatus.EXPIRED);

        when(certificateRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(expired));

        assertTrue(certificateService.isCertificateExpired(deviceId));
    }
}
