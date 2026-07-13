package com.example.telecom.common.test;

import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

/**
 * Builder for creating {@link AlarmRecord} instances in tests.
 *
 * <p>All fields default to sensible alarm values so tests only need to
 * customise the fields relevant to their scenario.
 */
public class AlarmTestDataBuilder {

    private String alarmId = "ALM-001";
    private String deviceId = "DEV-001";
    private String metricId = "metric-DEV-001";
    private String metricType = "CPU_USAGE";
    private Severity severity = Severity.MAJOR;
    private AlarmStatus status = AlarmStatus.OPEN;
    private String description = "CPU usage high on device DEV-001";
    private String alarmRegionCode = "REG-001";
    private long createdTime = System.currentTimeMillis();

    /** Creates a new builder with default values. */
    public AlarmTestDataBuilder() {
    }

    /** @param alarmId unique alarm identifier */
    public AlarmTestDataBuilder withAlarmId(final String alarmId) {
        this.alarmId = alarmId;
        return this;
    }

    /** @param deviceId the device that raised the alarm */
    public AlarmTestDataBuilder withDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    /** @param metricId the metric that triggered the alarm */
    public AlarmTestDataBuilder withMetricId(final String metricId) {
        this.metricId = metricId;
        return this;
    }

    /** @param metricType the metric type string */
    public AlarmTestDataBuilder withMetricType(final String metricType) {
        this.metricType = metricType;
        return this;
    }

    /** @param severity severity level */
    public AlarmTestDataBuilder withSeverity(final Severity severity) {
        this.severity = severity;
        return this;
    }

    /** @param status alarm status */
    public AlarmTestDataBuilder withStatus(final AlarmStatus status) {
        this.status = status;
        return this;
    }

    /** @param description human-readable alarm description */
    public AlarmTestDataBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    /** @param alarmRegionCode region code of the alarm */
    public AlarmTestDataBuilder withAlarmRegionCode(final String alarmRegionCode) {
        this.alarmRegionCode = alarmRegionCode;
        return this;
    }

    /** @param createdTime epoch-millis timestamp when the alarm was created */
    public AlarmTestDataBuilder withCreatedTime(final long createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    /**
     * Builds a single {@link AlarmRecord} from the current builder state.
     *
     * @return a new AlarmRecord instance
     */
    public AlarmRecord build() {
        return new AlarmRecord(alarmId, deviceId, metricId, metricType,
                severity, status, description, alarmRegionCode, createdTime);
    }

    /**
     * Shortcut that configures the builder for a {@link Severity#CRITICAL} alarm.
     *
     * @return this builder with critical-alarm presets applied
     */
    public AlarmTestDataBuilder buildCritical() {
        this.severity = Severity.CRITICAL;
        this.metricType = "SERVICE_DOWN";
        this.status = AlarmStatus.OPEN;
        this.description = "CRITICAL: Service down on device " + deviceId;
        return this;
    }
}
