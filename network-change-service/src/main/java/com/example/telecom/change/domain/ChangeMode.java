package com.example.telecom.change.domain;

/**
 * Mode under which a change is executed. Affects whether executors
 * actually apply changes or only simulate them.
 */
public enum ChangeMode {
    /** Apply the change to live equipment. */
    LIVE,
    /** Simulate the change without side effects. */
    DRY_RUN,
    /** Roll back an already applied change. */
    ROLLBACK,
    /** Safe mode — apply with additional validation checks. */
    SAFE
}
