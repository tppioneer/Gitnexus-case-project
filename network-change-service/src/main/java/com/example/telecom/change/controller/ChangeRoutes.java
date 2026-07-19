package com.example.telecom.change.controller;

/**
 * Route path constants — used by controllers for constant-based route mapping.
 * The PENDING constant is referenced by a @GetMapping annotation attribute.
 */
public final class ChangeRoutes {
    private ChangeRoutes() {}

    /** Path constant used by @GetMapping(path=ChangeRoutes.PENDING). */
    public static final String PENDING = "/pending";

    /** Path constant used in documentation strings — NOT a real route. */
    public static final String DRAFT = "/draft";

    /** Path used in log messages — NOT a real route. */
    public static final String ARCHIVED = "/archived";
}
