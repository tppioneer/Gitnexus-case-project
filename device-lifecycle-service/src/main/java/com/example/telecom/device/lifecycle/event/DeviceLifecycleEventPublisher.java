package com.example.telecom.device.lifecycle.event;

import com.example.telecom.device.lifecycle.dto.DeviceLifecycleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeviceLifecycleEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DeviceLifecycleEventPublisher.class);

    private final ApplicationEventPublisher eventPublisher;

    public DeviceLifecycleEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishRegistered(String deviceId) {
        DeviceLifecycleEvent event = new DeviceLifecycleEvent(deviceId, null, "REGISTERED", null, "system");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing registered event for device: {}", deviceId);
        eventPublisher.publishEvent(event);
    }

    public void publishActivated(String deviceId) {
        DeviceLifecycleEvent event = new DeviceLifecycleEvent(deviceId, "REGISTERED", "ACTIVE", null, "system");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing activated event for device: {}", deviceId);
        eventPublisher.publishEvent(event);
    }

    public void publishSuspended(String deviceId) {
        DeviceLifecycleEvent event = new DeviceLifecycleEvent(deviceId, "ACTIVE", "SUSPENDED", null, "system");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing suspended event for device: {}", deviceId);
        eventPublisher.publishEvent(event);
    }

    public void publishRetired(String deviceId) {
        DeviceLifecycleEvent event = new DeviceLifecycleEvent(deviceId, "ACTIVE", "RETIRED", null, "system");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing retired event for device: {}", deviceId);
        eventPublisher.publishEvent(event);
    }

    public void publishDecommissioned(String deviceId) {
        DeviceLifecycleEvent event = new DeviceLifecycleEvent(deviceId, "RETIRED", "DECOMMISSIONED", null, "system");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing decommissioned event for device: {}", deviceId);
        eventPublisher.publishEvent(event);
    }

    public void publishStateChanged(String deviceId, String previousStatus, String newStatus, String performedBy) {
        DeviceLifecycleEvent event = new DeviceLifecycleEvent(deviceId, previousStatus, newStatus, null, performedBy);
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing state change event for device: {} from {} to {}", deviceId, previousStatus, newStatus);
        eventPublisher.publishEvent(event);
    }

    public void publishBulkRegistrationComplete(int deviceCount) {
        log.info("Bulk registration completed for {} devices", deviceCount);
    }
}
