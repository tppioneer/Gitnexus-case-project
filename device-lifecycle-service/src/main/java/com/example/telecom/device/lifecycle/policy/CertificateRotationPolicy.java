package com.example.telecom.device.lifecycle.policy;

import com.example.telecom.device.lifecycle.model.DeviceCertStatus;
import com.example.telecom.device.lifecycle.model.DeviceCertificate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class CertificateRotationPolicy {

    private static final int ROTATION_PERIOD_DAYS = 365;
    private static final int GRACE_PERIOD_DAYS = 30;
    private static final int WARNING_THRESHOLD_DAYS = 60;

    public boolean evaluate(DeviceCertificate certificate) {
        if (certificate == null) {
            return false;
        }
        return shouldRotate(certificate);
    }

    public boolean shouldRotate(DeviceCertificate certificate) {
        if (certificate == null || certificate.getExpiresAt() == null) {
            return false;
        }
        if (certificate.getStatus() == DeviceCertStatus.REVOKED
                || certificate.getStatus() == DeviceCertStatus.EXPIRED) {
            return true;
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), certificate.getExpiresAt());
        return daysUntilExpiry <= WARNING_THRESHOLD_DAYS;
    }

    public int getRotationPeriodDays() {
        return ROTATION_PERIOD_DAYS;
    }

    public int getGracePeriodDays() {
        return GRACE_PERIOD_DAYS;
    }

    public int getWarningThresholdDays() {
        return WARNING_THRESHOLD_DAYS;
    }

    public boolean isExpired(DeviceCertificate certificate) {
        if (certificate == null || certificate.getExpiresAt() == null) {
            return false;
        }
        return certificate.getExpiresAt().isBefore(LocalDate.now());
    }

    public boolean isWithinGracePeriod(DeviceCertificate certificate) {
        if (certificate == null || certificate.getExpiresAt() == null) {
            return false;
        }
        long daysSinceExpiry = ChronoUnit.DAYS.between(certificate.getExpiresAt(), LocalDate.now());
        return daysSinceExpiry >= 0 && daysSinceExpiry <= GRACE_PERIOD_DAYS;
    }

    public boolean requiresImmediateRotation(DeviceCertificate certificate) {
        if (certificate == null || certificate.getExpiresAt() == null) {
            return false;
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), certificate.getExpiresAt());
        return daysUntilExpiry <= 7;
    }
}
