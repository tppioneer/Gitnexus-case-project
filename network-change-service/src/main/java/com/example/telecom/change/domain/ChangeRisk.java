package com.example.telecom.change.domain;

/**
 * Risk level of a network change. Determines approval requirements
 * and which {@code ChangeExecutor} strategy is selected.
 */
public enum ChangeRisk {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
