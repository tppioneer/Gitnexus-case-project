package com.example.telecom.collector.service;

import com.example.telecom.collector.adapter.VendorAlarmAdapterRegistry;
import com.example.telecom.collector.dto.NormalizedAlarm;
import com.example.telecom.common.exception.ValidationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SnmpTrapReceiver {

    private static final Pattern TRAP_HEADER_PATTERN = Pattern.compile(
            "^TRAP\\[(?<version>v1|v2c|v3)\\](?:@(?<community>[^:]+))?:(?<vendor>[^:]+):(?<trapId>[^:]+):(.+)$"
    );

    private static final Pattern TRAP_VARBIND_PATTERN = Pattern.compile(
            "(?<oid>\\d+(?:\\.\\d+)*)\\s*=\\s*(?<type>INTEGER|STRING|OID|IPADDRESS|COUNTER|GAUGE|TIMETICKS|OCTETS)\"?\\s*(?<value>.+?)\"?\\s*"
    );

    private final Map<String, TrapRecord> trapHistory = new ConcurrentHashMap<>();
    private final Map<String, TrapRecord> pendingTraps = new ConcurrentHashMap<>();
    private final VendorAlarmAdapterRegistry adapterRegistry;

    public SnmpTrapReceiver(VendorAlarmAdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    public NormalizedAlarm receive(String trapData) {
        if (!validateTrap(trapData)) {
            throw new ValidationException("trapData", "Invalid SNMP trap data format");
        }

        TrapRecord record = parseTrap(trapData);
        pendingTraps.put(record.getTrapId(), record);
        trapHistory.put(record.getTrapId(), record);

        NormalizedAlarm normalized = null;
        try {
            normalized = adapterRegistry.normalize(record.getVendor(), trapData);
            record.setStatus("PROCESSED");
            record.setNormalizedAlarm(normalized);
        } catch (Exception e) {
            record.setStatus("ERROR");
            record.setErrorMessage(e.getMessage());
        }

        return normalized;
    }

    public TrapRecord parseTrap(String trapData) {
        if (trapData == null || trapData.isBlank()) {
            throw new ValidationException("trapData", "Trap data must not be null or blank");
        }

        Matcher matcher = TRAP_HEADER_PATTERN.matcher(trapData.trim());
        if (!matcher.matches()) {
            throw new ValidationException("trapData", "Unrecognized trap format");
        }

        String trapId = matcher.group("trapId");
        String version = matcher.group("version");
        String community = matcher.group("community");
        String vendor = matcher.group("vendor");
        String payload = matcher.group(4);

        TrapRecord record = new TrapRecord();
        record.setTrapId(trapId);
        record.setVersion(version);
        record.setCommunity(community != null ? community : "public");
        record.setVendor(vendor);
        record.setRawData(trapData);
        record.setReceivedAt(System.currentTimeMillis());
        record.setStatus("RECEIVED");

        Map<String, String> varbinds = extractVarbinds(payload);
        record.setVarbinds(varbinds);

        return record;
    }

    public boolean acknowledgeTrap(String trapId) {
        TrapRecord record = pendingTraps.remove(trapId);
        if (record == null) {
            TrapRecord historyRecord = trapHistory.get(trapId);
            if (historyRecord == null) {
                return false;
            }
            historyRecord.setStatus("ACKNOWLEDGED");
            return true;
        }
        record.setStatus("ACKNOWLEDGED");
        return true;
    }

    public List<TrapRecord> getTrapHistory() {
        return trapHistory.values().stream()
                .sorted((a, b) -> Long.compare(b.getReceivedAt(), a.getReceivedAt()))
                .collect(Collectors.toList());
    }

    public List<TrapRecord> getPendingTraps() {
        return new ArrayList<>(pendingTraps.values());
    }

    public Optional<TrapRecord> getTrapById(String trapId) {
        return Optional.ofNullable(trapHistory.get(trapId));
    }

    private boolean validateTrap(String trapData) {
        if (trapData == null || trapData.isBlank()) {
            return false;
        }
        if (trapData.length() > 65536) {
            return false;
        }
        if (!trapData.startsWith("TRAP[")) {
            return false;
        }
        return true;
    }

    private Map<String, String> extractVarbinds(String payload) {
        Map<String, String> varbinds = new LinkedHashMap<>();
        if (payload == null || payload.isBlank()) {
            return varbinds;
        }
        Matcher matcher = TRAP_VARBIND_PATTERN.matcher(payload);
        while (matcher.find()) {
            String oid = matcher.group("oid");
            String value = matcher.group("value");
            varbinds.put(oid, value != null ? value.trim() : "");
        }
        if (varbinds.isEmpty()) {
            varbinds.put("payload", payload.trim());
        }
        return varbinds;
    }

    public static class TrapRecord {
        private String trapId;
        private String version;
        private String community;
        private String vendor;
        private String rawData;
        private long receivedAt;
        private String status;
        private String errorMessage;
        private Map<String, String> varbinds;
        private NormalizedAlarm normalizedAlarm;

        public TrapRecord() {
            this.varbinds = new LinkedHashMap<>();
        }

        public String getTrapId() { return trapId; }
        public void setTrapId(String trapId) { this.trapId = trapId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getCommunity() { return community; }
        public void setCommunity(String community) { this.community = community; }
        public String getVendor() { return vendor; }
        public void setVendor(String vendor) { this.vendor = vendor; }
        public String getRawData() { return rawData; }
        public void setRawData(String rawData) { this.rawData = rawData; }
        public long getReceivedAt() { return receivedAt; }
        public void setReceivedAt(long receivedAt) { this.receivedAt = receivedAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Map<String, String> getVarbinds() { return varbinds; }
        public void setVarbinds(Map<String, String> varbinds) { this.varbinds = varbinds; }
        public NormalizedAlarm getNormalizedAlarm() { return normalizedAlarm; }
        public void setNormalizedAlarm(NormalizedAlarm normalizedAlarm) { this.normalizedAlarm = normalizedAlarm; }
    }
}
