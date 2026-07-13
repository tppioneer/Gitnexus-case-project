package com.example.telecom.common.test;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Builder for creating {@link DeviceInfo} instances in tests.
 *
 * <p>All fields are pre-populated with sensible defaults so callers only need
 * to override the fields relevant to their test scenario.
 */
public class DeviceTestDataBuilder {

    private String deviceId = "DEV-001";
    private String deviceName = "Device-DEV-001";
    private DeviceType deviceType = DeviceType.BASE_STATION;
    private String vendor = "Cisco";
    private String maintenanceRegionCode = "REG-001";
    private String siteCode = "SITE-001";
    private String managementIp = "10.0.1.1";
    private boolean active = true;

    /** Creates a new builder with default values. */
    public DeviceTestDataBuilder() {
    }

    /** @param deviceId unique device identifier */
    public DeviceTestDataBuilder withDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    /** @param deviceName human-readable device name */
    public DeviceTestDataBuilder withDeviceName(final String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    /** @param deviceType type of device */
    public DeviceTestDataBuilder withDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    /** @param vendor device vendor/manufacturer */
    public DeviceTestDataBuilder withVendor(final String vendor) {
        this.vendor = vendor;
        return this;
    }

    /** @param maintenanceRegionCode maintenance region code */
    public DeviceTestDataBuilder withMaintenanceRegionCode(final String maintenanceRegionCode) {
        this.maintenanceRegionCode = maintenanceRegionCode;
        return this;
    }

    /** @param siteCode site code */
    public DeviceTestDataBuilder withSiteCode(final String siteCode) {
        this.siteCode = siteCode;
        return this;
    }

    /** @param managementIp management IP address */
    public DeviceTestDataBuilder withManagementIp(final String managementIp) {
        this.managementIp = managementIp;
        return this;
    }

    /** @param active whether the device is active */
    public DeviceTestDataBuilder withActive(final boolean active) {
        this.active = active;
        return this;
    }

    /**
     * Builds a single {@link DeviceInfo} from the current builder state.
     *
     * @return a new DeviceInfo instance
     */
    public DeviceInfo build() {
        return new DeviceInfo(deviceId, deviceName, deviceType, vendor,
                maintenanceRegionCode, siteCode, managementIp, active);
    }

    /**
     * Builds a list of devices with sequential identifiers.
     *
     * <p>The first device uses the current builder state.  Subsequent devices
     * copy the current state but replace {@code deviceId} and {@code deviceName}
     * with a zero-padded sequential suffix.
     *
     * @param count number of devices to create
     * @return list of {@code count} DeviceInfo instances
     * @throws IllegalArgumentException if count is negative
     */
    public List<DeviceInfo> buildList(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be non-negative, got: " + count);
        }
        final List<DeviceInfo> list = new ArrayList<>(count);
        IntStream.range(0, count).forEach(i -> {
            final String suffix = String.format("%03d", i + 1);
            list.add(new DeviceInfo(
                    "DEV-" + suffix,
                    "Device-" + suffix,
                    deviceType,
                    vendor,
                    maintenanceRegionCode,
                    siteCode,
                    managementIp,
                    active
            ));
        });
        return list;
    }
}
