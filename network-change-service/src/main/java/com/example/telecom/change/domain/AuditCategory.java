package com.example.telecom.change.domain;

/**
 * Category used by the audit subsystem to classify recorded actions.
 */
public enum AuditCategory {
    CHANGE,
    APPROVAL,
    ROLLBACK,
    NOTIFICATION,
    QUERY,
    CONFIGURATION
}
