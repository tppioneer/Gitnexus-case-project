package com.example.telecom.dispatch.domain;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.common.dispatch.DispatchPriority;

import java.time.LocalDateTime;
import java.util.List;

public class DispatchContext {

    private final DispatchOrder order;
    private final List<String> availableOperators;
    private final String regionCode;
    private final DispatchPriority priority;
    private final List<String> requiredSkills;
    private final LocalDateTime currentTime;

    public DispatchContext(DispatchOrder order, List<String> availableOperators,
                           String regionCode, DispatchPriority priority,
                           List<String> requiredSkills, LocalDateTime currentTime) {
        this.order = order;
        this.availableOperators = availableOperators;
        this.regionCode = regionCode;
        this.priority = priority;
        this.requiredSkills = requiredSkills;
        this.currentTime = currentTime;
    }

    public DispatchOrder getOrder() {
        return order;
    }

    public List<String> getAvailableOperators() {
        return availableOperators;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public DispatchPriority getPriority() {
        return priority;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public boolean hasRequiredSkills() {
        return requiredSkills != null && !requiredSkills.isEmpty();
    }

    public boolean hasAvailableOperators() {
        return availableOperators != null && !availableOperators.isEmpty();
    }
}
