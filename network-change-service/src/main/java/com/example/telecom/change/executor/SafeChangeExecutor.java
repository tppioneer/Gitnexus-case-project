package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Safe-mode executor — NOT marked with {@code @ChangeExecutorCandidate}.
 * This means it is excluded from the registry's candidate list even though
 * it implements {@code ChangeExecutor}. Used to test that custom qualifiers
 * correctly narrow the candidate set.
 */
@Component("safeExecutor")
public class SafeChangeExecutor extends AbstractChangeExecutor {

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
        if (context.getMode() == null) {
            throw new IllegalStateException("Safe executor requires an explicit mode");
        }
    }

    @Override
    protected ExecutionResult apply(ChangeContext context) {
        return ExecutionResult.success("SafeChangeExecutor",
                "Safe change applied for " + context.getChangeId());
    }
}
