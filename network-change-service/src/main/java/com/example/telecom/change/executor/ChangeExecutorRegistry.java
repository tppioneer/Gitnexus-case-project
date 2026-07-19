package com.example.telecom.change.executor;

import com.example.telecom.change.annotation.ChangeExecutorCandidate;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import com.example.telecom.change.domain.ChangeContext;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry that resolves a {@code ChangeExecutor} by device family.
 * The candidate set is determined at injection time by the custom
 * {@code @ChangeExecutorCandidate} qualifier — only annotated
 * implementations are collected.
 */
@Component
public class ChangeExecutorRegistry {

    private final Map<DeviceFamily, ChangeExecutor> byFamily;
    private final List<ChangeExecutor> allCandidates;

    public ChangeExecutorRegistry(@ChangeExecutorCandidate List<ChangeExecutor> candidates) {
        this.allCandidates = List.copyOf(candidates);
        this.byFamily = new EnumMap<>(DeviceFamily.class);
        for (ChangeExecutor executor : candidates) {
            byFamily.put(executor.supports(), executor);
        }
    }

    /** Resolve the executor for the given device family. */
    public ChangeExecutor resolve(DeviceFamily family) {
        return Optional.ofNullable(byFamily.get(family))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No registered executor for family: " + family));
    }

    /** Return all registered candidates. */
    public List<ChangeExecutor> allCandidates() {
        return allCandidates;
    }

    /** Check whether a family has a registered executor. */
    public boolean hasExecutor(DeviceFamily family) {
        return byFamily.containsKey(family);
    }
}
