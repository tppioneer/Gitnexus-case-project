package com.example.telecom.device.lifecycle.policy;

import com.example.telecom.common.device.DeviceType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FirmwareUpgradePolicy {

    private static final Map<String, String> MINIMUM_VERSIONS = new HashMap<>();
    private static final Map<String, String> RECOMMENDED_VERSIONS = new HashMap<>();
    private static final Map<String, Integer> UPGRADE_PRIORITIES = new HashMap<>();

    static {
        MINIMUM_VERSIONS.put("BASE_STATION", "1.0.0");
        MINIMUM_VERSIONS.put("OLT", "2.0.0");
        MINIMUM_VERSIONS.put("ROUTER", "3.1.0");
        MINIMUM_VERSIONS.put("SWITCH", "2.5.0");

        RECOMMENDED_VERSIONS.put("BASE_STATION", "3.2.1");
        RECOMMENDED_VERSIONS.put("OLT", "4.1.0");
        RECOMMENDED_VERSIONS.put("ROUTER", "5.0.0");
        RECOMMENDED_VERSIONS.put("SWITCH", "3.0.0");

        UPGRADE_PRIORITIES.put("BASE_STATION", 1);
        UPGRADE_PRIORITIES.put("OLT", 2);
        UPGRADE_PRIORITIES.put("ROUTER", 3);
        UPGRADE_PRIORITIES.put("SWITCH", 4);
    }

    public boolean isUpgradeAllowed(String deviceId, String targetVersion) {
        if (deviceId == null || targetVersion == null) {
            return false;
        }
        return targetVersion.matches("^\\d+\\.\\d+\\.\\d+$");
    }

    public String getRequiredVersion(String deviceType) {
        if (deviceType == null) {
            return "1.0.0";
        }
        return MINIMUM_VERSIONS.getOrDefault(deviceType, "1.0.0");
    }

    public String getRecommendedVersion(String deviceType) {
        if (deviceType == null) {
            return "1.0.0";
        }
        return RECOMMENDED_VERSIONS.getOrDefault(deviceType, "1.0.0");
    }

    public boolean isCompatible(String currentVersion, String targetVersion) {
        if (currentVersion == null || targetVersion == null) {
            return false;
        }
        int comparison = compareVersions(targetVersion, currentVersion);
        return comparison > 0;
    }

    public int getUpgradePriority(String deviceType) {
        if (deviceType == null) {
            return 99;
        }
        return UPGRADE_PRIORITIES.getOrDefault(deviceType, 99);
    }

    public boolean isMajorUpgrade(String currentVersion, String targetVersion) {
        if (currentVersion == null || targetVersion == null) {
            return false;
        }
        String[] currentParts = currentVersion.split("\\.");
        String[] targetParts = targetVersion.split("\\.");
        if (currentParts.length < 1 || targetParts.length < 1) {
            return false;
        }
        int currentMajor = Integer.parseInt(currentParts[0]);
        int targetMajor = Integer.parseInt(targetParts[0]);
        return targetMajor > currentMajor;
    }

    public boolean isMinorUpgrade(String currentVersion, String targetVersion) {
        if (currentVersion == null || targetVersion == null) {
            return false;
        }
        String[] currentParts = currentVersion.split("\\.");
        String[] targetParts = targetVersion.split("\\.");
        if (currentParts.length < 2 || targetParts.length < 2) {
            return false;
        }
        int currentMajor = Integer.parseInt(currentParts[0]);
        int targetMajor = Integer.parseInt(targetParts[0]);
        if (targetMajor != currentMajor) {
            return false;
        }
        int currentMinor = Integer.parseInt(currentParts[1]);
        int targetMinor = Integer.parseInt(targetParts[1]);
        return targetMinor > currentMinor;
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int len = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < len; i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (p1 != p2) {
                return p1 - p2;
            }
        }
        return 0;
    }
}
