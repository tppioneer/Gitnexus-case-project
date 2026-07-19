package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;

/**
 * Abstract base class for concrete change executors. Implements the
 * template method pattern:
 *
 * <pre>
 *   execute(context) → executeTemplate(context)
 *     → validate(context)    [abstract, overridden per device]
 *     → apply(context)       [abstract, overridden per device]
 *     → audit(context, result) [optional override, default logs]
 * </pre>
 *
 * Concrete executors override {@code validate} and {@code apply} to
 * implement device-specific behavior; the {@code execute} method
 * delegates to {@code executeTemplate} which orchestrates the sequence.
 */
public abstract class AbstractChangeExecutor implements ChangeExecutor {

    /** Template method — orchestrates validate → apply → audit. */
    protected final ExecutionResult executeTemplate(ChangeContext context) {
        validate(context);
        ExecutionResult result = apply(context);
        audit(context, result);
        return result;
    }

    /** Validates the change context before execution. */
    protected abstract void validate(ChangeContext context);

    /** Applies the change — produces the execution result. */
    protected abstract ExecutionResult apply(ChangeContext context);

    /** Default audit hook — subclasses may override. */
    protected void audit(ChangeContext context, ExecutionResult result) {
        // Default: no-op audit. Subclasses can override to record audit data.
    }
}
