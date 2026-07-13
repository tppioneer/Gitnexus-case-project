package com.example.telecom.device.lifecycle.event;

import com.example.telecom.device.lifecycle.dto.DeviceCertificateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DeviceCertificateEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeviceCertificateEventConsumer.class);

    @EventListener
    public void onCertificateIssued(DeviceCertificateEvent event) {
        log.info("Certificate issued for device: {}, certificate: {}",
                event.getDeviceId(), event.getCertificateId());
    }

    @EventListener
    public void onCertificateExpiring(DeviceCertificateEvent event) {
        log.warn("Certificate expiring for device: {}, certificate: {} - {}",
                event.getDeviceId(), event.getCertificateId(), event.getMessage());
    }

    @EventListener
    public void onCertificateRevoked(DeviceCertificateEvent event) {
        log.info("Certificate revoked for device: {}, certificate: {}",
                event.getDeviceId(), event.getCertificateId());
    }
}
