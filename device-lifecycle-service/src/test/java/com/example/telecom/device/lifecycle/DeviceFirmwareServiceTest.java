package com.example.telecom.device.lifecycle;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareRequest;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareResponse;
import com.example.telecom.device.lifecycle.model.DeviceFirmwareVersion;
import com.example.telecom.device.lifecycle.model.FirmwareUpgradeStatus;
import com.example.telecom.device.lifecycle.policy.FirmwareUpgradePolicy;
import com.example.telecom.device.lifecycle.repository.DeviceFirmwareRepository;
import com.example.telecom.device.lifecycle.service.DeviceFirmwareService;
import com.example.telecom.device.lifecycle.validator.DeviceLifecycleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceFirmwareServiceTest {

    @Mock
    private DeviceFirmwareRepository firmwareRepository;

    @Mock
    private FirmwareUpgradePolicy upgradePolicy;

    private DeviceLifecycleValidator validator;
    private DeviceFirmwareService firmwareService;

    @BeforeEach
    void setUp() {
        validator = new DeviceLifecycleValidator();
        firmwareService = new DeviceFirmwareService(firmwareRepository, upgradePolicy, validator);
    }

    @Test
    void testUpgradeFirmware() {
        String deviceId = "DEV-TEST001";
        DeviceFirmwareRequest request = new DeviceFirmwareRequest(deviceId, "2.1.0", null, null);
        when(firmwareRepository.findLatestByDeviceId(deviceId)).thenReturn(
                Optional.of(new DeviceFirmwareVersion(UUID.randomUUID().toString(), deviceId,
                        "1.0.0", "0.0.0", FirmwareUpgradeStatus.COMPLETED,
                        LocalDateTime.now().minusDays(30), "Initial")));
        when(upgradePolicy.isCompatible("1.0.0", "2.1.0")).thenReturn(true);
        when(upgradePolicy.isUpgradeAllowed(deviceId, "2.1.0")).thenReturn(true);
        when(firmwareRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        DeviceFirmwareResponse response = firmwareService.upgrade(request);
        assertNotNull(response);
        assertEquals(deviceId, response.getDeviceId());
    }

    @Test
    void testIncompatibleVersion() {
        String deviceId = "DEV-TEST003";
        DeviceFirmwareRequest request = new DeviceFirmwareRequest(deviceId, "0.5.0", null, null);
        when(firmwareRepository.findLatestByDeviceId(deviceId)).thenReturn(
                Optional.of(new DeviceFirmwareVersion(UUID.randomUUID().toString(), deviceId,
                        "1.0.0", "0.0.0", FirmwareUpgradeStatus.COMPLETED,
                        LocalDateTime.now().minusDays(30), "Initial")));
        when(upgradePolicy.isCompatible("1.0.0", "0.5.0")).thenReturn(false);
        assertThrows(DomainException.class, () -> firmwareService.upgrade(request));
    }

    @Test
    void testGetUpgradeHistory() {
        String deviceId = "DEV-TEST004";
        DeviceFirmwareVersion v1 = new DeviceFirmwareVersion(UUID.randomUUID().toString(), deviceId,
                "1.0.0", "0.0.0", FirmwareUpgradeStatus.COMPLETED,
                LocalDateTime.now().minusDays(60), "Initial");
        when(firmwareRepository.findByDeviceId(deviceId)).thenReturn(List.of(v1));
        List<DeviceFirmwareVersion> result = firmwareService.getUpgradeHistory(deviceId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
