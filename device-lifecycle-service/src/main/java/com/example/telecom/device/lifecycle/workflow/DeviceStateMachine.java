package com.example.telecom.device.lifecycle.workflow;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.model.DeviceLifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DeviceStateMachine {

    private static final Logger log = LoggerFactory.getLogger(DeviceStateMachine.class);

    private final Map<DeviceLifecycleState, Set<DeviceLifecycleState>> transitions;

    public DeviceStateMachine() {
        transitions = new EnumMap<>(DeviceLifecycleState.class);
        transitions.put(DeviceLifecycleState.REGISTERED, Set.of(DeviceLifecycleState.ACTIVE));
        transitions.put(DeviceLifecycleState.ACTIVE, Set.of(DeviceLifecycleState.SUSPENDED,
                DeviceLifecycleState.RETIRED, DeviceLifecycleState.FIRMWARE_UPGRADING));
        transitions.put(DeviceLifecycleState.SUSPENDED, Set.of(DeviceLifecycleState.ACTIVE));
        transitions.put(DeviceLifecycleState.RETIRED, Set.of(DeviceLifecycleState.DECOMMISSIONED));
        transitions.put(DeviceLifecycleState.DECOMMISSIONED, Set.of());
        transitions.put(DeviceLifecycleState.FIRMWARE_UPGRADING, Set.of(DeviceLifecycleState.ACTIVE,
                DeviceLifecycleState.FAILED));
        transitions.put(DeviceLifecycleState.FAILED, Set.of(DeviceLifecycleState.ACTIVE));
    }

    public boolean isValidTransition(DeviceLifecycleState from, DeviceLifecycleState to) {
        Set<DeviceLifecycleState> allowed = transitions.get(from);
        return allowed != null && allowed.contains(to);
    }

    public DeviceLifecycleState transition(DeviceLifecycleState from, DeviceLifecycleState to, String deviceId) {
        validateTransition(from, to);
        log.info("Device {} transitioning from {} to {}", deviceId, from, to);
        return to;
    }

    public DeviceLifecycleState transition(String deviceId, DeviceLifecycleState target) {
        log.info("Device {} transitioning to {}", deviceId, target);
        return target;
    }

    public Set<DeviceLifecycleState> getNextStates(DeviceLifecycleState current) {
        Set<DeviceLifecycleState> allowed = transitions.get(current);
        if (allowed == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(allowed);
    }

    public List<DeviceLifecycleState> getAllStates() {
        List<DeviceLifecycleState> states = new ArrayList<>(transitions.keySet());
        Collections.sort(states, (a, b) -> a.name().compareTo(b.name()));
        return states;
    }

    private void validateTransition(DeviceLifecycleState from, DeviceLifecycleState to) {
        if (!isValidTransition(from, to)) {
            throw new DomainException("INVALID_TRANSITION",
                    String.format("Invalid state transition from %s to %s", from, to));
        }
    }

    public boolean hasNextStates(DeviceLifecycleState current) {
        Set<DeviceLifecycleState> allowed = transitions.get(current);
        return allowed != null && !allowed.isEmpty();
    }

    public int getTransitionCount(DeviceLifecycleState state) {
        Set<DeviceLifecycleState> allowed = transitions.get(state);
        return allowed == null ? 0 : allowed.size();
    }

    public boolean canTransitionFrom(DeviceLifecycleState state) {
        Set<DeviceLifecycleState> allowed = transitions.get(state);
        return allowed != null && !allowed.isEmpty();
    }

    public boolean canTransitionTo(DeviceLifecycleState state) {
        return transitions.values().stream()
                .anyMatch(set -> set.contains(state));
    }

    public List<DeviceLifecycleState> getSourceStates(DeviceLifecycleState target) {
        List<DeviceLifecycleState> sources = new ArrayList<>();
        for (Map.Entry<DeviceLifecycleState, Set<DeviceLifecycleState>> entry : transitions.entrySet()) {
            if (entry.getValue().contains(target)) {
                sources.add(entry.getKey());
            }
        }
        return sources;
    }
}
