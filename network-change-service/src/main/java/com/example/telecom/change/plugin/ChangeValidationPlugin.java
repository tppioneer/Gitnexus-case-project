package com.example.telecom.change.plugin;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;

/**
 * SPI interface for validation plugins loaded via {@code ServiceLoader}.
 * Plugins are discovered from {@code META-INF/services/} at runtime.
 */
public interface ChangeValidationPlugin {

    /** Human-readable plugin name. */
    String name();

    /** Validate the change context — return true if valid. */
    boolean validate(ChangeContext context);

    /** Execute the plugin's custom validation logic. */
    ExecutionResult execute(ChangeContext context);
}
