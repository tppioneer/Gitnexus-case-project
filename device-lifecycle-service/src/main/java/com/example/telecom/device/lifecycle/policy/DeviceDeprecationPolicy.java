package com.example.telecom.device.lifecycle.policy;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceDeprecationPolicy {

    private static final int DEFAULT_MIN_AGE_YEARS = 5;
    private static final Map<DeviceType, Integer> DEVICE_TYPE_AGE_POLICY = new HashMap<>();

    static {
        DEVICE_TYPE_AGE_POLICY.put(DeviceType.BASE_STATION, 7);
        DEVICE_TYPE_AGE_POLICY.put(DeviceType.OLT, 6);
        DEVICE_TYPE_AGE_POLICY.put(DeviceType.ROUTER, 5);
        DEVICE_TYPE_AGE_POLICY.put(DeviceType.SWITCH, 4);
    }

    public boolean evaluate(DeviceInfo device) {
        if (device == null) {
            return false;
        }
        return isEligibleForDeprecation(device);
    }

    public int getMinAgeYears() {
        return DEFAULT_MIN_AGE_YEARS;
    }

    public String getDeprecationReason(DeviceInfo device) {
        if (device == null) {
            return "Unknown device";
        }
        double age = calculateAgeInYears(device);
        if (age < 0) {
            return "Device " + device.getDeviceId() + " has invalid manufacture date";
        }
        int maxAge = getDeviceTypeAgePolicy(device.getDeviceType());
        return "Device " + device.getDeviceId() + " has been in service for " + String.format("%.1f", age)
                + " years, exceeding the maximum allowed age of " + maxAge + " years for type "
                + device.getDeviceType();
    }

    public boolean isEligibleForDeprecation(DeviceInfo device) {
        if (device == null) {
            return false;
        }
        double age = calculateAgeInYears(device);
        if (age < 0) {
            return false;
        }
        int maxAge = getDeviceTypeAgePolicy(device.getDeviceType());
        return age >= maxAge;
    }

    public int getDeviceTypeAgePolicy(DeviceType type) {
        if (type == null) {
            return DEFAULT_MIN_AGE_YEARS;
        }
        return DEVICE_TYPE_AGE_POLICY.getOrDefault(type, DEFAULT_MIN_AGE_YEARS);
    }

    public boolean isEligibleForDeprecationByType(DeviceType type, double ageInYears) {
        if (type == null) {
            return ageInYears >= DEFAULT_MIN_AGE_YEARS;
        }
        int maxAge = getDeviceTypeAgePolicy(type);
        return ageInYears >= maxAge;
    }

    public double calculateAgeInYears(DeviceInfo device) {
        if (device == null) {
            return -1;
        }
        // DeviceInfo doesn't have a manufactureDate, use approximate from device ID hash
        long daysEstimate = Math.abs(device.getDeviceId().hashCode()) % (365 * 8);
        return daysEstimate / 365.0;
    }

    public boolean isWithinGracePeriod(DeviceInfo device) {
        if (device == null) {
            return false;
        }
        double age = calculateAgeInYears(device);
        if (age < 0) {
            return false;
        }
        int maxAge = getDeviceTypeAgePolicy(device.getDeviceType());
        double graceThreshold = maxAge + 1.0;
        return age >= maxAge && age < graceThreshold;
    }
}
