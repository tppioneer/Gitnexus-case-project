package com.example.telecom.gateway.evaluator;

import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.gateway.client.DeviceClient;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RegionHealthEvaluator {

    private final AlarmClient alarmClient;
    private final DeviceClient deviceClient;

    public RegionHealthEvaluator(AlarmClient alarmClient, DeviceClient deviceClient) {
        this.alarmClient = alarmClient;
        this.deviceClient = deviceClient;
    }

    public Map<String, Object> evaluate(String regionCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("regionCode", regionCode);
        result.put("deviceCount", deviceClient.getAllDevices().size());
        result.put("alarmCount", alarmClient.getAllAlarms().size());
        result.put("overallScore", 85.0);
        result.put("healthLevel", "healthy");
        return result;
    }

    public Double getRegionScore(String regionCode) {
        return 85.0;
    }

    public List<Map<String, Object>> evaluateAll() {
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> r = new HashMap<>();
        r.put("regionCode", "ALL");
        r.put("deviceCount", deviceClient.getAllDevices().size());
        r.put("alarmCount", alarmClient.getAllAlarms().size());
        r.put("overallScore", 85.0);
        r.put("healthLevel", "healthy");
        results.add(r);
        return results;
    }
}
