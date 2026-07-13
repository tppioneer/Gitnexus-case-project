package com.example.telecom.collector.adapter;

import com.example.telecom.collector.dto.NormalizedAlarm;
import com.example.telecom.common.alarm.Severity;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiberHomeVendorAlarmAdapter implements VendorAlarmAdapter {

    private static final Pattern FH_ALARM_PATTERN = Pattern.compile(
            "^FH-ALERT:(?<deviceId>[^|]+)\\|(?<alarmCode>\\d+)\\|(?<severity>\\d)\\|(?<timestamp>\\d+)\\|(?<description>.+)$"
    );

    private static final Pattern FH_SYSLOG_PATTERN = Pattern.compile(
            "^FH:(?<deviceId>[^:]+):(?<module>[^:]+):(?<eventType>[^:]+):(?<description>.+)$"
    );

    private static final Pattern FH_OPTICAL_ALARM_PATTERN = Pattern.compile(
            "^FH-OPT:(?<deviceId>[^|]+)\\|(?<port>[^|]+)\\|(?<power>[-+]?\\d+\\.?\\d*)\\|(?<threshold>[-+]?\\d+\\.?\\d*)\\|(?<description>.+)$"
    );

    @Override
    public NormalizedAlarm normalize(String rawAlarm) {
        if (rawAlarm == null || rawAlarm.isBlank()) {
            throw new IllegalArgumentException("Raw alarm data must not be null or blank");
        }
        return parseFiberHomeFormat(rawAlarm);
    }

    @Override
    public String getVendorType() {
        return "FiberHome";
    }

    private NormalizedAlarm parseFiberHomeFormat(String rawAlarm) {
        Matcher matcher = FH_ALARM_PATTERN.matcher(rawAlarm.trim());
        if (matcher.matches()) {
            String description = matcher.group("description");
            String alarmCode = matcher.group("alarmCode");
            return buildNormalizedAlarm(
                    matcher.group("deviceId"),
                    "FH_ALARM_" + alarmCode,
                    resolveSeverityCode(Integer.parseInt(matcher.group("severity"))),
                    Long.parseLong(matcher.group("timestamp")),
                    "[" + alarmCode + "] " + description,
                    rawAlarm
            );
        }

        Matcher syslogMatcher = FH_SYSLOG_PATTERN.matcher(rawAlarm.trim());
        if (syslogMatcher.matches()) {
            String module = syslogMatcher.group("module");
            String eventType = syslogMatcher.group("eventType");
            return buildNormalizedAlarm(
                    syslogMatcher.group("deviceId"),
                    "FH_" + module + "_" + eventType,
                    Severity.WARNING,
                    System.currentTimeMillis(),
                    syslogMatcher.group("description"),
                    rawAlarm
            );
        }

        Matcher opticalMatcher = FH_OPTICAL_ALARM_PATTERN.matcher(rawAlarm.trim());
        if (opticalMatcher.matches()) {
            double power = Double.parseDouble(opticalMatcher.group("power"));
            double threshold = Double.parseDouble(opticalMatcher.group("threshold"));
            Severity severity = power < threshold ? Severity.CRITICAL : Severity.MAJOR;
            return buildNormalizedAlarm(
                    opticalMatcher.group("deviceId"),
                    "OPTICAL_POWER_ALARM",
                    severity,
                    System.currentTimeMillis(),
                    "Port " + opticalMatcher.group("port") + " optical power " + power + " dBm (threshold: " + threshold + " dBm)",
                    rawAlarm
            );
        }

        return buildNormalizedAlarm(
                "unknown",
                "UNKNOWN",
                Severity.INFO,
                System.currentTimeMillis(),
                "Unable to parse FiberHome alarm format: " + rawAlarm.substring(0, Math.min(100, rawAlarm.length())),
                rawAlarm
        );
    }

    private Severity resolveSeverityCode(int severityCode) {
        return switch (severityCode) {
            case 1 -> Severity.CRITICAL;
            case 2 -> Severity.MAJOR;
            case 3 -> Severity.WARNING;
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
