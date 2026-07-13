package com.example.telecom.gateway.evaluator;

import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.common.device.DeviceInfo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DeviceHealthEvaluator {

    private final DeviceClient deviceClient;

    public DeviceHealthEvaluator(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    public Map<String, Object> evaluate(String deviceId) {
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("overallHealth", 95.0);
        result.put("healthLevel", "healthy");
        return result;
    }

    public List<Map<String, Object>> evaluateBatch(List<String> deviceIds) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (String id : deviceIds) {
            results.add(evaluate(id));
        }
        return results;
    }

    public Map<String, Object> getHealthFactors(String deviceId) {
        Map<String, Object> factors = new HashMap<>();
        factors.put("deviceId", deviceId);
        return factors;
    }
}
