package com.example.telecom.device.lifecycle.event;

import com.example.telecom.device.lifecycle.dto.DeviceFirmwareEvent;
import com.example.telecom.device.lifecycle.service.DeviceFirmwareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DeviceFirmwareEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeviceFirmwareEventConsumer.class);

    private final DeviceFirmwareService firmwareService;

    public DeviceFirmwareEventConsumer(DeviceFirmwareService firmwareService) {
        this.firmwareService = firmwareService;
    }

    @EventListener
    public void onFirmwareUpgradeRequest(DeviceFirmwareEvent event) {
        log.info("Received firmware upgrade request event for device: {}, target: {}",
                event.getDeviceId(), event.getToVersion());
        String deviceId = event.getDeviceId();
        String targetVersion = event.getToVersion();
        log.info("Processing firmware upgrade for device {} to version {}", deviceId, targetVersion);
    }

    @EventListener
    public void onFirmwareUpgradeCompleted(DeviceFirmwareEvent event) {
        log.info("Firmware upgrade completed for device: {} from {} to {}",
                event.getDeviceId(), event.getFromVersion(), event.getToVersion());
    }

    @EventListener
    public void onFirmwareRollback(DeviceFirmwareEvent event) {
        log.info("Firmware rollback for device: {} to version {}",
                event.getDeviceId(), event.getToVersion());
    }
}
