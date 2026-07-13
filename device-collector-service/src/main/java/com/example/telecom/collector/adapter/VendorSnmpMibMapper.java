package com.example.telecom.collector.adapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VendorSnmpMibMapper {

    private final Map<String, MibEntry> mibDatabase = new ConcurrentHashMap<>();

    public VendorSnmpMibMapper() {
        loadMibDatabase();
    }

    public String mapOidToMetric(String oid) {
        if (oid == null || oid.isBlank()) {
            throw new IllegalArgumentException("OID must not be null or blank");
        }
        MibEntry entry = mibDatabase.get(oid.trim());
        if (entry != null) {
            return entry.getMetricName();
        }
        for (Map.Entry<String, MibEntry> e : mibDatabase.entrySet()) {
            if (oid.startsWith(e.getKey())) {
                return e.getValue().getMetricName();
            }
        }
        return "UNKNOWN_METRIC";
    }

    public String mapOidToAlarmType(String oid) {
        if (oid == null || oid.isBlank()) {
            throw new IllegalArgumentException("OID must not be null or blank");
        }
        MibEntry entry = mibDatabase.get(oid.trim());
        if (entry != null) {
            return entry.getAlarmType();
        }
        if (oid.startsWith("1.3.6.1.6.3.1.1.5")) {
            return switch (oid) {
                case "1.3.6.1.6.3.1.1.5.1" -> "COLD_START";
                case "1.3.6.1.6.3.1.1.5.2" -> "WARM_START";
                case "1.3.6.1.6.3.1.1.5.3" -> "LINK_DOWN";
                case "1.3.6.1.6.3.1.1.5.4" -> "LINK_UP";
                case "1.3.6.1.6.3.1.1.5.5" -> "AUTH_FAILURE";
                case "1.3.6.1.6.3.1.1.5.6" -> "EGP_NEIGHBOR_LOSS";
                default -> "SNMP_TRAP_" + oid.replace('.', '_');
            };
        }
        if (oid.startsWith("1.3.6.1.2.1.")) {
            return "RFC1213_" + oid.substring("1.3.6.1.2.1.".length()).replace('.', '_');
        }
        return "UNKNOWN_OID_" + oid.replace('.', '_');
    }

    public String getMibDescription(String oid) {
        if (oid == null || oid.isBlank()) {
            throw new IllegalArgumentException("OID must not be null or blank");
        }
        MibEntry entry = mibDatabase.get(oid.trim());
        if (entry != null) {
            return entry.getDescription();
        }
        if (oid.startsWith("1.3.6.1.2.1.1")) return "System information group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.2")) return "Interfaces group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.3")) return "Address translation group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.4")) return "IP group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.5")) return "ICMP group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.6")) return "TCP group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.7")) return "UDP group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.10")) return "Transmission group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.2.1.11")) return "SNMP group (RFC1213-MIB)";
        if (oid.startsWith("1.3.6.1.4.1.2011")) return "Huawei proprietary MIB";
        if (oid.startsWith("1.3.6.1.4.1.3902")) return "ZTE proprietary MIB";
        if (oid.startsWith("1.3.6.1.4.1.3200")) return "FiberHome proprietary MIB";
        if (oid.startsWith("1.3.6.1.6.3.1.1.5")) return "SNMPv2-MIB trap notifications";
        return "Unknown OID: " + oid;
    }

    public Map<String, MibEntry> getAllMappings() {
        return Collections.unmodifiableMap(mibDatabase);
    }

    public boolean containsOid(String oid) {
        return mibDatabase.containsKey(oid);
    }

    private void loadMibDatabase() {
        mibDatabase.put("1.3.6.1.2.1.1.3.0", new MibEntry("sysUpTime", "SYSTEM_UPTIME",
                "The time since the network management portion of the system was last re-initialized"));
        mibDatabase.put("1.3.6.1.2.1.1.5.0", new MibEntry("sysName", "SYSTEM_NAME",
                "An administratively-assigned name for this managed node"));
        mibDatabase.put("1.3.6.1.2.1.2.2.1.10", new MibEntry("ifInOctets", "INTERFACE_IN_OCTETS",
                "Total number of octets received on the interface"));
        mibDatabase.put("1.3.6.1.2.1.2.2.1.16", new MibEntry("ifOutOctets", "INTERFACE_OUT_OCTETS",
                "Total number of octets transmitted out of the interface"));
        mibDatabase.put("1.3.6.1.2.1.2.2.1.14", new MibEntry("ifInErrors", "INTERFACE_IN_ERRORS",
                "Number of inbound packets that contained errors"));
        mibDatabase.put("1.3.6.1.2.1.2.2.1.20", new MibEntry("ifOutErrors", "INTERFACE_OUT_ERRORS",
                "Number of outbound packets that could not be transmitted because of errors"));
        mibDatabase.put("1.3.6.1.2.1.2.2.1.8", new MibEntry("ifOperStatus", "INTERFACE_OPER_STATUS",
                "Current operational state of the interface"));
        mibDatabase.put("1.3.6.1.2.1.4.3.0", new MibEntry("ipInReceives", "IP_IN_RECEIVES",
                "Total number of input datagrams received from interfaces"));
        mibDatabase.put("1.3.6.1.2.1.4.10.0", new MibEntry("ipInDiscards", "IP_IN_DISCARDS",
                "Number of input IP datagrams discarded"));
        mibDatabase.put("1.3.6.1.2.1.6.7.0", new MibEntry("tcpCurrEstab", "TCP_CURR_ESTAB",
                "Number of TCP connections currently established"));
        mibDatabase.put("1.3.6.1.2.1.10.7.2.1.2", new MibEntry("hCInOctets", "HC_IN_OCTETS",
                "High-capacity counter for octets received"));
        mibDatabase.put("1.3.6.1.4.1.2011.6.1.2.1.1.2", new MibEntry("huaweiCpuUsage", "HW_CPU_USAGE",
                "Huawei device CPU usage percentage"));
        mibDatabase.put("1.3.6.1.4.1.2011.6.1.2.1.1.3", new MibEntry("huaweiMemoryUsage", "HW_MEMORY_USAGE",
                "Huawei device memory usage percentage"));
        mibDatabase.put("1.3.6.1.4.1.2011.6.1.2.1.1.5", new MibEntry("huaweiTemperature", "HW_TEMPERATURE",
                "Huawei device temperature reading"));
        mibDatabase.put("1.3.6.1.4.1.3902.3.1.1.1.6", new MibEntry("zteCpuLoad", "ZTE_CPU_LOAD",
                "ZTE device CPU load average"));
        mibDatabase.put("1.3.6.1.4.1.3902.3.1.1.1.7", new MibEntry("zteMemoryUtil", "ZTE_MEMORY_UTIL",
                "ZTE device memory utilization"));
        mibDatabase.put("1.3.6.1.4.1.3200.1.1.1.1.1", new MibEntry("fhOpticalPower", "FH_OPTICAL_POWER",
                "FiberHome optical transceiver power level"));
        mibDatabase.put("1.3.6.1.4.1.3200.1.1.1.1.2", new MibEntry("fhTemperature", "FH_TEMPERATURE",
                "FiberHome device temperature"));
        mibDatabase.put("1.3.6.1.6.3.1.1.5.3", new MibEntry("linkDown", "LINK_DOWN",
                "SNMP linkDown trap notification"));
        mibDatabase.put("1.3.6.1.6.3.1.1.5.4", new MibEntry("linkUp", "LINK_UP",
                "SNMP linkUp trap notification"));
        mibDatabase.put("1.3.6.1.6.3.1.1.5.5", new MibEntry("authenticationFailure", "AUTH_FAILURE",
                "SNMP authentication failure trap notification"));
    }

    public static class MibEntry {
        private final String metricName;
        private final String alarmType;
        private final String description;

        public MibEntry(String metricName, String alarmType, String description) {
            this.metricName = metricName;
            this.alarmType = alarmType;
            this.description = description;
        }

        public String getMetricName() { return metricName; }
        public String getAlarmType() { return alarmType; }
        public String getDescription() { return description; }
    }
}
