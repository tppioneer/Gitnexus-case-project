package com.example.telecom.change.plugin;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;

/**
 * Concrete validation plugin — registered in META-INF/services for
 * ServiceLoader discovery. This class must have a no-arg constructor
 * to be loaded reflectively.
 */
public class BenchmarkChangeValidationPlugin implements ChangeValidationPlugin {

    public BenchmarkChangeValidationPlugin() {}

    @Override
    public String name() {
        return "BenchmarkChangeValidation";
    }

    @Override
    public boolean validate(ChangeContext context) {
        return context.getChangeId() != null && !context.getChangeId().isBlank();
    }

    @Override
    public ExecutionResult execute(ChangeContext context) {
        if (!validate(context)) {
            return ExecutionResult.failure(name(), "Validation failed for " + context.getChangeId());
        }
        return ExecutionResult.success(name(),
                "Benchmark validation passed for " + context.getChangeId());
    }
}
