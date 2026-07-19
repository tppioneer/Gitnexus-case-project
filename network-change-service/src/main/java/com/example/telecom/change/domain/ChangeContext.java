package com.example.telecom.change.domain;

import com.example.telecom.change.dto.ChangeRequest;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable execution context passed to {@code ChangeExecutor} implementations.
 * Encapsulates all inputs required for a single change execution attempt.
 */
public final class ChangeContext {
    private final String changeId;
    private final DeviceFamily deviceFamily;
    private final ChangeMode mode;
    private final String regionCode;
    private final String operatorId;
    private final Instant requestedAt;

    public ChangeContext(String changeId, DeviceFamily family) {
        this(changeId, family, ChangeMode.LIVE, "default", "system", Instant.now());
    }

    public ChangeContext(String changeId, DeviceFamily family, ChangeMode mode) {
        this(changeId, family, mode, "default", "system", Instant.now());
    }

    public ChangeContext(String changeId, DeviceFamily family, ChangeMode mode,
                         String regionCode, String operatorId, Instant requestedAt) {
        this.changeId = Objects.requireNonNull(changeId, "changeId");
        this.deviceFamily = Objects.requireNonNull(family, "deviceFamily");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.regionCode = regionCode;
        this.operatorId = operatorId;
        this.requestedAt = requestedAt;
    }

    /**
     * Constructor taking a {@code ChangeRequest} DTO. Demonstrates overload
     * resolution across different argument types — the caller decides at
     * compile time which overload applies.
     */
    public ChangeContext(ChangeRequest request) {
        this(Objects.requireNonNull(request, "request").getTitle() == null
                ? "CHG-AUTO" : "CHG-" + request.getTitle().hashCode(),
             request.getDeviceFamily() == null ? DeviceFamily.ALL : request.getDeviceFamily(),
             request.getMode() == null ? ChangeMode.LIVE : request.getMode(),
             request.getRegionCode() == null ? "default" : request.getRegionCode(),
             "system",
             Instant.now());
    }

    public String getChangeId() { return changeId; }
    public DeviceFamily getDeviceFamily() { return deviceFamily; }
    public ChangeMode getMode() { return mode; }
    public String getRegionCode() { return regionCode; }
    public String getOperatorId() { return operatorId; }
    public Instant getRequestedAt() { return requestedAt; }

    /** Convenience alias used by some call sites — equivalent to getDeviceFamily(). */
    public DeviceFamily deviceFamily() { return deviceFamily; }

    @Override
    public String toString() {
        return "ChangeContext{changeId='" + changeId + "', family=" + deviceFamily + ", mode=" + mode + "}";
    }
}
