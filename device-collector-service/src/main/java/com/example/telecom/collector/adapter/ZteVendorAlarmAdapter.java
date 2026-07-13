package com.example.telecom.collector.adapter;

import com.example.telecom.collector.dto.NormalizedAlarm;
import com.example.telecom.common.alarm.Severity;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZteVendorAlarmAdapter implements VendorAlarmAdapter {

    private static final Pattern ZTE_ALARM_PATTERN = Pattern.compile(
            "^ZTE:ALARM:(?<deviceId>[^:]+):(?<alarmId>[^:]+):(?<alarmType>[^:]+):(?<severity>[^:]+):(?<timestamp>\\d+):(?<description>.+)$"
    );

    private static final Pattern ZTE_SNMP_PATTERN = Pattern.compile(
            "^ZTE-SNMP:(?<oid>[^:]+):(?<deviceId>[^:]+):(?<severity>[^:]+):(?<description>.+)$"
    );

    private static final Pattern ZTE_TRAP_PATTERN = Pattern.compile(
            "^ZTE-TRAP:v1:(?<deviceId>[^:]+):(?<generic>[^:]+):(?<specific>[^:]+):(?<timestamp>\\d+):(?<description>.+)$"
    );

    @Override
    public NormalizedAlarm normalize(String rawAlarm) {
        if (rawAlarm == null || rawAlarm.isBlank()) {
            throw new IllegalArgumentException("Raw alarm data must not be null or blank");
        }
        return parseZteFormat(rawAlarm);
    }

    @Override
    public String getVendorType() {
        return "ZTE";
    }

    private NormalizedAlarm parseZteFormat(String rawAlarm) {
        Matcher matcher = ZTE_ALARM_PATTERN.matcher(rawAlarm.trim());
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

        Matcher snmpMatcher = ZTE_SNMP_PATTERN.matcher(rawAlarm.trim());
        if (snmpMatcher.matches()) {
            String oid = snmpMatcher.group("oid");
            String alarmType = mapOidToAlarmType(oid);
            return buildNormalizedAlarm(
                    snmpMatcher.group("deviceId"),
                    alarmType,
                    resolveSeverity(snmpMatcher.group("severity")),
                    System.currentTimeMillis(),
                    snmpMatcher.group("description") + " [OID: " + oid + "]",
                    rawAlarm
            );
        }

        Matcher trapMatcher = ZTE_TRAP_PATTERN.matcher(rawAlarm.trim());
        if (trapMatcher.matches()) {
            return buildNormalizedAlarm(
                    trapMatcher.group("deviceId"),
                    "TRAP_" + trapMatcher.group("specific"),
                    resolveSeverityByTrapCode(trapMatcher.group("generic"), trapMatcher.group("specific")),
                    Long.parseLong(trapMatcher.group("timestamp")),
                    trapMatcher.group("description"),
                    rawAlarm
            );
        }

        return buildNormalizedAlarm(
                "unknown",
                "UNKNOWN",
                Severity.INFO,
                System.currentTimeMillis(),
                "Unable to parse ZTE alarm format: " + rawAlarm.substring(0, Math.min(100, rawAlarm.length())),
                rawAlarm
        );
    }

    private Severity resolveSeverity(String severityStr) {
        if (severityStr == null) return Severity.INFO;
        return switch (severityStr.toUpperCase()) {
            case "CRITICAL", "CRIT", "1" -> Severity.CRITICAL;
            case "MAJOR", "MAJ", "2" -> Severity.MAJOR;
            case "WARNING", "WARN", "MINOR", "3" -> Severity.WARNING;
            case "CLEAR", "INFO", "4", "5" -> Severity.INFO;
            default -> Severity.INFO;
        };
    }

    private Severity resolveSeverityByTrapCode(String generic, String specific) {
        int genericCode = 0;
        try {
            genericCode = Integer.parseInt(generic);
        } catch (NumberFormatException e) {
            return Severity.INFO;
        }
        if (genericCode >= 5) {
            return Severity.CRITICAL;
        } else if (genericCode >= 3) {
            return Severity.MAJOR;
        } else if (genericCode >= 1) {
            return Severity.WARNING;
        }
        return Severity.INFO;
    }

    private String mapOidToAlarmType(String oid) {
        if (oid == null) return "UNKNOWN";
        if (oid.startsWith("1.3.6.1.4.1.3902.")) return "ZTE_PROPRIETARY";
        if (oid.contains("linkDown") || oid.contains("1.3.6.1.6.3.1.1.5.3")) return "LINK_DOWN";
        if (oid.contains("linkUp") || oid.contains("1.3.6.1.6.3.1.1.5.4")) return "LINK_UP";
        if (oid.contains("authenticationFailure")) return "AUTH_FAILURE";
        return "SNMP_TRAP_" + oid.replace('.', '_');
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
