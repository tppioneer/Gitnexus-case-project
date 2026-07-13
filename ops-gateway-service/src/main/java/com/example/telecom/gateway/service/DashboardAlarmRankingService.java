package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.common.alarm.AlarmRecord;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardAlarmRankingService {

    private final AlarmClient alarmClient;
    private final DeviceClient deviceClient;

    public DashboardAlarmRankingService(AlarmClient alarmClient, DeviceClient deviceClient) {
        this.alarmClient = alarmClient;
        this.deviceClient = deviceClient;
    }

    public List<Map<String, Object>> getAlarmRanking(int limit) {
        List<AlarmRecord> alarms = alarmClient.getAllAlarms();
        Map<String, List<AlarmRecord>> grouped = alarms.stream()
                .collect(Collectors.groupingBy(
                    a -> a.getMetricType() != null ? a.getMetricType() : "unknown"));
        List<Map<String, Object>> rankings = new ArrayList<>();
        for (Map.Entry<String, List<AlarmRecord>> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey());
            item.put("count", entry.getValue().size());
            rankings.add(item);
        }
        return rankings.stream().limit(limit).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDeviceRanking(int limit) {
        return deviceClient.getAllDevices().stream()
                .limit(limit)
                .map(d -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("deviceId", d.getDeviceId());
                    item.put("deviceName", d.getDeviceName());
                    item.put("vendor", d.getVendor());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRegionRanking(int limit) {
        List<Map<String, Object>> rankings = new ArrayList<>();
        Map<String, Long> regionCount = alarmClient.getAllAlarms().stream()
                .filter(a -> a.getAlarmRegionCode() != null)
                .collect(Collectors.groupingBy(AlarmRecord::getAlarmRegionCode, Collectors.counting()));
        for (Map.Entry<String, Long> entry : regionCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("regionCode", entry.getKey());
            item.put("alarmCount", entry.getValue());
            rankings.add(item);
        }
        rankings.sort((a, b) -> Long.compare((Long) b.get("alarmCount"), (Long) a.get("alarmCount")));
        return rankings.stream().limit(limit).collect(Collectors.toList());
    }
}
