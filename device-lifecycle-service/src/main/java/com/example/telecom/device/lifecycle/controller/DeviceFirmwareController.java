package com.example.telecom.device.lifecycle.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareRequest;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareResponse;
import com.example.telecom.device.lifecycle.model.DeviceFirmwareVersion;
import com.example.telecom.device.lifecycle.service.DeviceFirmwareService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/devices/firmware")
public class DeviceFirmwareController {

    private static final Logger log = LoggerFactory.getLogger(DeviceFirmwareController.class);

    private final DeviceFirmwareService firmwareService;

    public DeviceFirmwareController(DeviceFirmwareService firmwareService) {
        this.firmwareService = firmwareService;
    }

    @PostMapping("/upgrade")
    public ResponseEntity<ApiResponse<DeviceFirmwareResponse>> upgrade(
            @Valid @RequestBody DeviceFirmwareRequest request) {
        log.info("Firmware upgrade request for device: {} to version {}",
                request.getDeviceId(), request.getTargetVersion());
        DeviceFirmwareResponse response = firmwareService.upgrade(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{deviceId}/version")
    public ResponseEntity<ApiResponse<String>> getCurrentVersion(
            @PathVariable String deviceId) {
        log.info("Getting current firmware version for device: {}", deviceId);
        String version = firmwareService.getCurrentVersion(deviceId);
        return ResponseEntity.ok(ApiResponse.success(version));
    }

    @GetMapping("/history/{deviceId}")
    public ResponseEntity<ApiResponse<List<DeviceFirmwareVersion>>> getUpgradeHistory(
            @PathVariable String deviceId) {
        log.info("Getting firmware upgrade history for device: {}", deviceId);
        List<DeviceFirmwareVersion> history = firmwareService.getUpgradeHistory(deviceId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PostMapping("/rollback/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceFirmwareResponse>> rollback(
            @PathVariable String deviceId) {
        log.info("Firmware rollback request for device: {}", deviceId);
        DeviceFirmwareResponse response = firmwareService.rollback(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/compatibility/{deviceId}/{version}")
    public ResponseEntity<ApiResponse<Boolean>> checkCompatibility(
            @PathVariable String deviceId, @PathVariable String version) {
        boolean compatible = firmwareService.checkCompatibility(deviceId, version);
        return ResponseEntity.ok(ApiResponse.success(compatible));
    }

    @GetMapping("/{deviceId}/count")
    public ResponseEntity<ApiResponse<Long>> countUpgrades(
            @PathVariable String deviceId) {
        long count = firmwareService.countUpgrades(deviceId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/{deviceId}/has-history")
    public ResponseEntity<ApiResponse<Boolean>> hasUpgradeHistory(
            @PathVariable String deviceId) {
        boolean hasHistory = firmwareService.hasUpgradeHistory(deviceId);
        return ResponseEntity.ok(ApiResponse.success(hasHistory));
    }
}
