package com.example.telecom.device.lifecycle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "device.lifecycle")
public class LifecycleProperties {

    private int defaultActivationTimeoutHours = 24;
    private int maxConcurrentUpgrades = 10;
    private int certificateRotationDays = 365;
    private int deprecationAgeYears = 5;
    private int firmwareCheckIntervalMinutes = 60;

    public int getDefaultActivationTimeoutHours() {
        return defaultActivationTimeoutHours;
    }

    public void setDefaultActivationTimeoutHours(int defaultActivationTimeoutHours) {
        this.defaultActivationTimeoutHours = defaultActivationTimeoutHours;
    }

    public int getMaxConcurrentUpgrades() {
        return maxConcurrentUpgrades;
    }

    public void setMaxConcurrentUpgrades(int maxConcurrentUpgrades) {
        this.maxConcurrentUpgrades = maxConcurrentUpgrades;
    }

    public int getCertificateRotationDays() {
        return certificateRotationDays;
    }

    public void setCertificateRotationDays(int certificateRotationDays) {
        this.certificateRotationDays = certificateRotationDays;
    }

    public int getDeprecationAgeYears() {
        return deprecationAgeYears;
    }

    public void setDeprecationAgeYears(int deprecationAgeYears) {
        this.deprecationAgeYears = deprecationAgeYears;
    }

    public int getFirmwareCheckIntervalMinutes() {
        return firmwareCheckIntervalMinutes;
    }

    public void setFirmwareCheckIntervalMinutes(int firmwareCheckIntervalMinutes) {
        this.firmwareCheckIntervalMinutes = firmwareCheckIntervalMinutes;
    }
}
