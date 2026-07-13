package com.example.telecom.common.test;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.common.device.MetricType;
import com.example.telecom.common.maintenance.MaintenanceWindow;
import com.example.telecom.common.region.Region;
import com.example.telecom.common.user.NotificationPreference;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Central factory providing static methods to create common domain objects
 * populated with sensible defaults for use in tests.
 *
 * <p>Every method accepts key identifiers so callers can wire objects into
 * their desired relationships.  All temporal fields use {@link System#currentTimeMillis()}
 * unless a specific time is required.
 */
public final class TestDataFactory {

    private static final long DEFAULT_TIME = System.currentTimeMillis();

    private TestDataFactory() {
        // utility class
    }

    // ---------------------------------------------------------------
    // DeviceInfo
    // ---------------------------------------------------------------

    /**
     * Creates a {@link DeviceInfo} with the given identity and maintenance region.
     *
     * @param deviceId            unique device identifier
     * @param maintenanceRegionCode maintenance region code the device belongs to
     * @return a fully populated DeviceInfo
     */
    public static DeviceInfo createDeviceInfo(final String deviceId, final String maintenanceRegionCode) {
        final DeviceInfo di = new DeviceInfo();
        di.setDeviceId(deviceId);
        di.setDeviceName("Device-" + deviceId);
        di.setDeviceType(DeviceType.BASE_STATION);
        di.setVendor("Cisco");
        di.setMaintenanceRegionCode(maintenanceRegionCode);
        di.setSiteCode("SITE-" + deviceId);
        di.setManagementIp("10.0.1." + (deviceId.hashCode() % 254 + 1));
        di.setActive(true);
        return di;
    }

    // ---------------------------------------------------------------
    // DeviceMetric
    // ---------------------------------------------------------------

    /**
     * Creates a {@link DeviceMetric} for the given device, metric type and value.
     *
     * @param deviceId the device that produced the metric
     * @param type     the metric type
     * @param value    the metric value
     * @return a fully populated DeviceMetric
     */
    public static DeviceMetric createDeviceMetric(final String deviceId, final MetricType type, final double value) {
        return new DeviceMetric(
                UUID.randomUUID().toString(),
                deviceId,
                type,
                value,
                "units",
                Instant.now(),
                "Cisco"
        );
    }

    // ---------------------------------------------------------------
    // AlarmRecord
    // ---------------------------------------------------------------

    /**
     * Creates an {@link AlarmRecord} with the given identifiers and severity.
     *
     * @param alarmId  unique alarm identifier
     * @param deviceId the device that raised the alarm
     * @param severity severity level
     * @return a fully populated AlarmRecord
     */
    public static AlarmRecord createAlarmRecord(final String alarmId, final String deviceId, final Severity severity) {
        return new AlarmRecord(
                alarmId,
                deviceId,
                "metric-" + deviceId,
                "CPU_USAGE",
                severity,
                AlarmStatus.OPEN,
                severity + " alarm on device " + deviceId,
                "REG-001",
                DEFAULT_TIME
        );
    }

    // ---------------------------------------------------------------
    // WorkOrder
    // ---------------------------------------------------------------

    /**
     * Creates a {@link WorkOrder} for the given work-order and device identifiers.
     *
     * @param workOrderId unique work-order identifier
     * @param deviceId    the device the work order relates to
     * @return a fully populated WorkOrder
     */
    public static WorkOrder createWorkOrder(final String workOrderId, final String deviceId) {
        final WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(workOrderId);
        wo.setAlarmId("alarm-" + deviceId);
        wo.setDeviceId(deviceId);
        wo.setStatus(WorkOrderStatus.CREATED);
        wo.setPriority(WorkOrderPriority.MEDIUM);
        wo.setTitle("Investigate device " + deviceId);
        wo.setDescription("Routine investigation of reported issues on device " + deviceId);
        wo.setMaintenanceRegionCode("REG-001");
        wo.setAssignee("engineer-" + deviceId);
        wo.setCreatedTime(DEFAULT_TIME);
        wo.setUpdatedTime(DEFAULT_TIME);
        return wo;
    }

    // ---------------------------------------------------------------
    // Region
    // ---------------------------------------------------------------

    /**
     * Creates a {@link Region} with the given code and name.
     *
     * @param regionCode region code
     * @param regionName human-readable region name
     * @return a fully populated Region
     */
    public static Region createRegion(final String regionCode, final String regionName) {
        return new Region(
                "reg-" + regionCode,
                regionName,
                regionCode,
                null
        );
    }

    // ---------------------------------------------------------------
    // DeviceMetricEvent
    // ---------------------------------------------------------------

    /**
     * Creates a {@link DeviceMetricEvent} with the given properties and a device region code.
     *
     * @param deviceId        the device that produced the metric
     * @param type            metric type as enum
     * @param value           metric value
     * @param deviceRegionCode device region code
     * @return a fully populated DeviceMetricEvent
     */
    public static DeviceMetricEvent createDeviceMetricEvent(final String deviceId, final MetricType type,
                                                            final double value, final String deviceRegionCode) {
        return new DeviceMetricEvent(
                UUID.randomUUID().toString(),
                deviceId,
                "metric-" + deviceId,
                type.name(),
                value,
                "units",
                DEFAULT_TIME,
                deviceRegionCode
        );
    }

    // ---------------------------------------------------------------
    // AlarmEvent
    // ---------------------------------------------------------------

    /**
     * Creates an {@link AlarmEvent} with the given properties and device region code.
     *
     * @param alarmId          unique alarm identifier
     * @param deviceId         the device that raised the alarm
     * @param severity         severity level
     * @param deviceRegionCode device region code
     * @return a fully populated AlarmEvent
     */
    public static AlarmEvent createAlarmEvent(final String alarmId, final String deviceId,
                                              final Severity severity, final String deviceRegionCode) {
        return new AlarmEvent(
                UUID.randomUUID().toString(),
                alarmId,
                deviceId,
                severity,
                AlarmStatus.OPEN,
                deviceRegionCode,
                DEFAULT_TIME
        );
    }

    // ---------------------------------------------------------------
    // NotificationPreference
    // ---------------------------------------------------------------

    /**
     * Creates a {@link NotificationPreference} for the given user and channel.
     *
     * @param userId  user identifier
     * @param channel notification channel (e.g. EMAIL, SMS)
     * @param enabled whether the channel is enabled
     * @return a fully populated NotificationPreference
     */
    public static NotificationPreference createNotificationPreference(final String userId,
                                                                      final String channel,
                                                                      final boolean enabled) {
        final NotificationPreference pref = new NotificationPreference(userId, List.of(channel));
        if (!enabled) {
            pref.getChannelEnabled().put(channel, false);
        }
        return pref;
    }

    // ---------------------------------------------------------------
    // MaintenanceWindow
    // ---------------------------------------------------------------

    /**
     * Creates a {@link MaintenanceWindow} for the given window and device identifiers.
     *
     * @param windowId unique maintenance-window identifier
     * @param deviceId the device under maintenance
     * @return a fully populated MaintenanceWindow
     */
    public static MaintenanceWindow createMaintenanceWindow(final String windowId, final String deviceId) {
        return new MaintenanceWindow(
                windowId,
                deviceId,
                "REG-001",
                DEFAULT_TIME,
                DEFAULT_TIME + 7_200_000L, // +2 hours
                "Scheduled maintenance for device " + deviceId,
                true
        );
    }
}
