package com.example.telecom.change.executor;

import com.example.telecom.change.annotation.ChangeExecutorCandidate;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Executor activated only when the "dry-run" profile is active.
 * Used to test profile-conditional DI.
 */
@Profile("dry-run")
@Component
@ChangeExecutorCandidate
public class DryRunChangeExecutor extends AbstractChangeExecutor {

    @Override
    public DeviceFamily supports() {
        return DeviceFamily.ALL;
    }

    @Override
    public ExecutionResult execute(ChangeContext context) {
        return executeTemplate(context);
    }

    @Override
    protected void validate(ChangeContext context) {
        // Dry-run skips strict validation
    }

    @Override
    protected ExecutionResult apply(ChangeContext context) {
        return ExecutionResult.success("DryRunChangeExecutor",
                "Dry-run simulation for " + context.getChangeId());
    }
}
