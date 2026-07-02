package com.example.telecom.collector.service;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.collector.validator.DeviceRegistrationValidator;
import com.example.telecom.common.api.OperationResult;
import com.example.telecom.common.device.DeviceInfo;

import java.util.List;

public class DeviceBatchImportService {

    private final DeviceRegistryService deviceRegistryService;
    private final DeviceRegistryRepository deviceRegistryRepository;
    private final DeviceRegistrationValidator importValidator;

    public DeviceBatchImportService(DeviceRegistryService deviceRegistryService,
                                     DeviceRegistryRepository deviceRegistryRepository,
                                     DeviceRegistrationValidator importValidator) {
        this.deviceRegistryService = deviceRegistryService;
        this.deviceRegistryRepository = deviceRegistryRepository;
        this.importValidator = importValidator;
    }

    public OperationResult importDevices(List<DeviceRegistrationRequest> requests) {
        OperationResult result = new OperationResult("importDevices", requests.size());
        for (DeviceRegistrationRequest request : requests) {
            try {
                importValidator.validate(request);
                deviceRegistryService.register(request);
                result.recordSuccess();
            } catch (Exception e) {
                result.recordFailure("Import failed: " + e.getMessage());
            }
        }
        result.complete();
        return result;
    }

    public void process(List<DeviceRegistrationRequest> requests) {
        importDevices(requests);
    }

    public List<DeviceInfo> findDevicesByVendor(String vendor) {
        return deviceRegistryRepository.findAll().stream()
                .filter(d -> vendor.equals(d.getVendor()))
                .toList();
    }
}
