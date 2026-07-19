package com.example.telecom.change.domain;

/**
 * Lifecycle status of a network change plan.
 */
public enum ChangeStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    RUNNING,
    COMPLETED,
    FAILED,
    ROLLED_BACK
}
