package com.example.telecom.change.executor;

import com.example.telecom.change.annotation.ChangeExecutorCandidate;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Executor for router device family changes.
 */
@Component("routerExecutor")
@ChangeExecutorCandidate
public class RouterChangeExecutor extends AbstractChangeExecutor {

    @Override
    public DeviceFamily supports() {
        return DeviceFamily.ROUTER;
    }

    @Override
    public ExecutionResult execute(ChangeContext context) {
        return executeTemplate(context);
    }

    @Override
    protected void validate(ChangeContext context) {
        if (context.getChangeId() == null || context.getChangeId().isBlank()) {
            throw new IllegalArgumentException("Router change requires non-blank changeId");
        }
    }

    @Override
    protected ExecutionResult apply(ChangeContext context) {
        return ExecutionResult.success("RouterChangeExecutor",
                "Router change applied for " + context.getChangeId());
    }

    @Override
    protected void audit(ChangeContext context, ExecutionResult result) {
        // Record router-specific audit data
    }
}
