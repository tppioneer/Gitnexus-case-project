package com.example.telecom.device.lifecycle.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.device.DeviceLifecycleStatus;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleRequest;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleResponse;
import com.example.telecom.device.lifecycle.model.AuditEntry;
import com.example.telecom.device.lifecycle.service.DeviceLifecycleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/devices/lifecycle")
public class DeviceLifecycleController {

    private static final Logger log = LoggerFactory.getLogger(DeviceLifecycleController.class);

    private final DeviceLifecycleService lifecycleService;

    public DeviceLifecycleController(DeviceLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeviceLifecycleResponse>> register(
            @Valid @RequestBody DeviceLifecycleRequest request) {
        log.info("Registering device: {}", request.getDeviceId());
        DeviceLifecycleResponse response = lifecycleService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/activate/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceLifecycleResponse>> activate(
            @PathVariable String deviceId) {
        log.info("Activating device: {}", deviceId);
        DeviceLifecycleResponse response = lifecycleService.activate(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/suspend/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceLifecycleResponse>> suspend(
            @PathVariable String deviceId) {
        log.info("Suspending device: {}", deviceId);
        DeviceLifecycleResponse response = lifecycleService.suspend(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/retire/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceLifecycleResponse>> retire(
            @PathVariable String deviceId) {
        log.info("Retiring device: {}", deviceId);
        DeviceLifecycleResponse response = lifecycleService.retire(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/decommission/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceLifecycleResponse>> decommission(
            @PathVariable String deviceId) {
        log.info("Decommissioning device: {}", deviceId);
        DeviceLifecycleResponse response = lifecycleService.decommission(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceLifecycleResponse>> getStatus(
            @PathVariable String deviceId) {
        log.info("Getting lifecycle status for device: {}", deviceId);
        DeviceLifecycleResponse response = lifecycleService.getStatus(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<DeviceLifecycleResponse>>> listByStatus(
            @PathVariable DeviceLifecycleStatus status) {
        log.info("Listing devices by status: {}", status);
        List<DeviceLifecycleResponse> devices = lifecycleService.listByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(devices));
    }

    @GetMapping("/{deviceId}/history")
    public ResponseEntity<ApiResponse<List<AuditEntry>>> getHistory(
            @PathVariable String deviceId) {
        log.info("Getting lifecycle history for device: {}", deviceId);
        List<AuditEntry> history = lifecycleService.getLifecycleHistory(deviceId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalCount() {
        long count = lifecycleService.getTotalDeviceCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<ApiResponse<Long>> countByStatus(
            @PathVariable DeviceLifecycleStatus status) {
        long count = lifecycleService.countDevicesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DeviceLifecycleResponse>>> listAll() {
        List<DeviceLifecycleResponse> devices = lifecycleService.listAllDevices();
        return ResponseEntity.ok(ApiResponse.success(devices));
    }

    @GetMapping("/check/{deviceId}")
    public ResponseEntity<ApiResponse<Boolean>> checkRegistered(
            @PathVariable String deviceId) {
        boolean registered = lifecycleService.isDeviceRegistered(deviceId);
        return ResponseEntity.ok(ApiResponse.success(registered));
    }
}
