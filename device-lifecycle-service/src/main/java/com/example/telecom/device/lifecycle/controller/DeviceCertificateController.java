package com.example.telecom.device.lifecycle.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateRequest;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateResponse;
import com.example.telecom.device.lifecycle.model.DeviceCertificate;
import com.example.telecom.device.lifecycle.service.DeviceCertificateService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/devices/certificates")
public class DeviceCertificateController {

    private static final Logger log = LoggerFactory.getLogger(DeviceCertificateController.class);

    private final DeviceCertificateService certificateService;

    public DeviceCertificateController(DeviceCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/issue")
    public ResponseEntity<ApiResponse<DeviceCertificateResponse>> issue(
            @Valid @RequestBody DeviceCertificateRequest request) {
        log.info("Issuing certificate for device: {}", request.getDeviceId());
        DeviceCertificateResponse response = certificateService.issue(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/renew/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceCertificateResponse>> renew(
            @PathVariable String deviceId) {
        log.info("Renewing certificate for device: {}", deviceId);
        DeviceCertificateResponse response = certificateService.renew(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/revoke/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceCertificateResponse>> revoke(
            @PathVariable String deviceId) {
        log.info("Revoking certificate for device: {}", deviceId);
        DeviceCertificateResponse response = certificateService.revoke(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceCertificateResponse>> getCertificate(
            @PathVariable String deviceId) {
        log.info("Getting certificate info for device: {}", deviceId);
        DeviceCertificateResponse response = certificateService.getCertificate(deviceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<DeviceCertificate>>> listExpiring(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Listing certificates expiring within {} days", days);
        List<DeviceCertificate> expiring = certificateService.listExpiring(days);
        return ResponseEntity.ok(ApiResponse.success(expiring));
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<ApiResponse<Long>> countByStatus(
            @PathVariable com.example.telecom.device.lifecycle.model.DeviceCertStatus status) {
        long count = certificateService.countCertificatesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/check/{deviceId}/active")
    public ResponseEntity<ApiResponse<Boolean>> hasActiveCertificate(
            @PathVariable String deviceId) {
        boolean hasActive = certificateService.hasActiveCertificate(deviceId);
        return ResponseEntity.ok(ApiResponse.success(hasActive));
    }

    @GetMapping("/check/{deviceId}/expired")
    public ResponseEntity<ApiResponse<Boolean>> isCertificateExpired(
            @PathVariable String deviceId) {
        boolean expired = certificateService.isCertificateExpired(deviceId);
        return ResponseEntity.ok(ApiResponse.success(expired));
    }
}
