package com.example.telecom.collector.adapter;

import com.example.telecom.collector.dto.NormalizedAlarm;
import com.example.telecom.common.alarm.Severity;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HuaweiVendorAlarmAdapter implements VendorAlarmAdapter {

    private static final Pattern HUAWEI_ALARM_PATTERN = Pattern.compile(
            "^HUAWEI::ALARM::(?<deviceId>[^:]+)::(?<alarmType>[^:]+)::(?<severity>[^:]+)::(?<timestamp>\\d+)::(?<description>.+)$"
    );

    private static final Pattern HUAWEI_SYSLOG_PATTERN = Pattern.compile(
            "^<(?<priority>\\d+)>(?<timestampStr>[^|]+)\\|(?<deviceId>[^|]+)\\|(?<alarmType>[^|]+)\\|(?<severity>[^|]+)\\|(?<description>.+)$"
    );

    @Override
    public NormalizedAlarm normalize(String rawAlarm) {
        if (rawAlarm == null || rawAlarm.isBlank()) {
            throw new IllegalArgumentException("Raw alarm data must not be null or blank");
        }
        return parseHuaweiFormat(rawAlarm);
    }

    @Override
    public String getVendorType() {
        return "Huawei";
    }

    private NormalizedAlarm parseHuaweiFormat(String rawAlarm) {
        Matcher matcher = HUAWEI_ALARM_PATTERN.matcher(rawAlarm.trim());
        if (matcher.matches()) {
            return buildNormalizedAlarm(
                    matcher.group("deviceId"),
                    matcher.group("alarmType"),
                    resolveSeverity(matcher.group("severity")),
                    Long.parseLong(matcher.group("timestamp")),
                    matcher.group("description"),
                    rawAlarm
            );
        }

        Matcher syslogMatcher = HUAWEI_SYSLOG_PATTERN.matcher(rawAlarm.trim());
        if (syslogMatcher.matches()) {
            return buildNormalizedAlarm(
                    syslogMatcher.group("deviceId"),
                    syslogMatcher.group("alarmType"),
                    resolveSeverity(syslogMatcher.group("severity")),
                    System.currentTimeMillis(),
                    syslogMatcher.group("description"),
                    rawAlarm
            );
        }

        return buildNormalizedAlarm(
                "unknown",
                "UNKNOWN",
                Severity.INFO,
                System.currentTimeMillis(),
                "Unable to parse Huawei alarm format: " + rawAlarm.substring(0, Math.min(100, rawAlarm.length())),
                rawAlarm
        );
    }

    private Severity resolveSeverity(String severityStr) {
        if (severityStr == null) return Severity.INFO;
        return switch (severityStr.toUpperCase()) {
            case "CRITICAL", "CRIT", "1" -> Severity.CRITICAL;
            case "MAJOR", "MAJ", "2" -> Severity.MAJOR;
            case "WARNING", "WARN", "MINOR", "3" -> Severity.WARNING;
            case "INFO", "CLEAR", "4" -> Severity.INFO;
            default -> Severity.INFO;
        };
    }

    private NormalizedAlarm buildNormalizedAlarm(String deviceId, String alarmType,
                                                  Severity severity, long timestamp,
                                                  String description, String rawAlarm) {
        return new NormalizedAlarm(
                UUID.randomUUID().toString(),
                alarmType,
                severity,
                deviceId,
                timestamp,
                description,
                getVendorType(),
                rawAlarm
        );
    }
}
