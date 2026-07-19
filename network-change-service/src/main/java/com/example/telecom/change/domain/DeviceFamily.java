package com.example.telecom.change.domain;

/**
 * Device family classification used to dispatch change execution to the
 * correct {@code ChangeExecutor} implementation.
 *
 * This enum is intentionally parallel to, but distinct from, the common-domain
 * {@code DeviceType} so that the change module has its own dispatch axis.
 */
public enum DeviceFamily {
    ROUTER,
    TRANSMISSION,
    RADIO,
    ALL
}
