package com.example.telecom.collector.controller;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.service.CollectorAuditService;
import com.example.telecom.collector.service.DeviceRegistryService;
import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.device.DeviceInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceRegistryController {

    private final DeviceRegistryService deviceRegistryService;
    private final CollectorAuditService collectorAuditService;

    public DeviceRegistryController(DeviceRegistryService deviceRegistryService,
                                     CollectorAuditService collectorAuditService) {
        this.deviceRegistryService = deviceRegistryService;
        this.collectorAuditService = collectorAuditService;
    }

    @PostMapping("/register")
    public ApiResponse<DeviceInfo> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        DeviceInfo deviceInfo = deviceRegistryService.register(request);
        collectorAuditService.logDeviceRegistered(deviceInfo.getDeviceId(), "system");
        return ApiResponse.success(deviceInfo);
    }

    @GetMapping
    public ApiResponse<List<DeviceInfo>> listDevices(
            @RequestParam(required = false) String regionCode) {
        if (regionCode != null) {
            return ApiResponse.success(deviceRegistryService.findByRegionCode(regionCode));
        }
        return ApiResponse.success(deviceRegistryService.findAllDevices());
    }

    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceInfo> getDevice(@PathVariable String deviceId) {
        return ApiResponse.success(deviceRegistryService.findActiveDevice(deviceId));
    }
}
