package com.example.telecom.change.executor;

import com.example.telecom.change.annotation.ChangeExecutorCandidate;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Executor for transmission (DWDM/OTN) device family changes.
 */
@Component("transmissionExecutor")
@ChangeExecutorCandidate
public class TransmissionChangeExecutor extends AbstractChangeExecutor {

    @Override
    public DeviceFamily supports() {
        return DeviceFamily.TRANSMISSION;
    }

    @Override
    public ExecutionResult execute(ChangeContext context) {
        return executeTemplate(context);
    }

    @Override
    protected void validate(ChangeContext context) {
        if (context.getChangeId() == null || context.getChangeId().isBlank()) {
            throw new IllegalArgumentException("Transmission change requires non-blank changeId");
        }
    }

    @Override
    protected ExecutionResult apply(ChangeContext context) {
        return ExecutionResult.success("TransmissionChangeExecutor",
                "Transmission change applied for " + context.getChangeId());
    }
}
