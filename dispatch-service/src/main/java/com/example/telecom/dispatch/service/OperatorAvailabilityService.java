package com.example.telecom.dispatch.service;

import com.example.telecom.dispatch.domain.Operator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OperatorAvailabilityService {

    private final Map<String, String> operatorNames = new ConcurrentHashMap<>();
    private final Map<String, String> operatorRegions = new ConcurrentHashMap<>();
    private final Map<String, Boolean> availabilityStatus = new ConcurrentHashMap<>();
    private final Map<String, Integer> operatorLoads = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        operatorNames.put("OP-001", "Alice");
        operatorNames.put("OP-002", "Bob");
        operatorNames.put("OP-003", "Charlie");
        operatorNames.put("OP-004", "Diana");
        operatorNames.put("OP-005", "Eve");

        operatorRegions.put("OP-001", "REGION_EAST");
        operatorRegions.put("OP-002", "REGION_WEST");
        operatorRegions.put("OP-003", "REGION_NORTH");
        operatorRegions.put("OP-004", "REGION_SOUTH");
        operatorRegions.put("OP-005", "REGION_CENTRAL");

        for (String id : operatorNames.keySet()) {
            availabilityStatus.put(id, false);
            operatorLoads.put(id, 0);
        }
    }

    public boolean check(String operatorId) {
        return availabilityStatus.getOrDefault(operatorId, false);
    }

    public void setAvailable(String operatorId) {
        availabilityStatus.put(operatorId, true);
    }

    public void setBusy(String operatorId) {
        availabilityStatus.put(operatorId, false);
    }

    public void setOffline(String operatorId) {
        availabilityStatus.put(operatorId, false);
    }

    public List<Operator> getAvailableOperators() {
        List<Operator> available = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : availabilityStatus.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                String id = entry.getKey();
                String name = operatorNames.getOrDefault(id, id);
                String region = operatorRegions.getOrDefault(id, "UNKNOWN");
                int load = operatorLoads.getOrDefault(id, 0);
                available.add(new Operator(id, name, region, true, load));
            }
        }
        return available;
    }

    public int getOperatorLoad(String operatorId) {
        return operatorLoads.getOrDefault(operatorId, 0);
    }

    public void incrementLoad(String operatorId) {
        operatorLoads.merge(operatorId, 1, Integer::sum);
    }

    public void decrementLoad(String operatorId) {
        operatorLoads.merge(operatorId, -1, Integer::sum);
    }
}
