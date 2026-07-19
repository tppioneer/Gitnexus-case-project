package com.example.telecom.change.executor;

import com.example.telecom.change.annotation.ChangeExecutorCandidate;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Executor for radio (RAN) device family changes.
 * Marked as {@code @Primary} so that when a bare {@code ChangeExecutor}
 * is injected without a qualifier, Spring selects this implementation.
 */
@Primary
@Component("radioExecutor")
@ChangeExecutorCandidate
public class RadioChangeExecutor extends AbstractChangeExecutor {

    @Override
    public DeviceFamily supports() {
        return DeviceFamily.RADIO;
    }

    @Override
    public ExecutionResult execute(ChangeContext context) {
        return executeTemplate(context);
    }

    @Override
    protected void validate(ChangeContext context) {
        if (context.getChangeId() == null || context.getChangeId().isBlank()) {
            throw new IllegalArgumentException("Radio change requires non-blank changeId");
        }
    }

    @Override
    protected ExecutionResult apply(ChangeContext context) {
        return ExecutionResult.success("RadioChangeExecutor",
                "Radio change applied for " + context.getChangeId());
    }
}
