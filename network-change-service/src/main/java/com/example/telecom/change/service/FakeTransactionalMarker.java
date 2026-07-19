package com.example.telecom.change.service;

/**
 * Plain Java class whose name contains "Transactional". It is not a
 * Spring annotation and does not participate in transaction management.
 */
public class FakeTransactionalMarker {

    /** Returns the marker string — a plain string, not an annotation application. */
    public String getMarkerName() {
        return "@Transactional";
    }

    /** Returns a propagation name as a plain string — not a Spring {@code Propagation} enum value. */
    public String getPropagation() {
        return "REQUIRES_NEW";
    }
}
