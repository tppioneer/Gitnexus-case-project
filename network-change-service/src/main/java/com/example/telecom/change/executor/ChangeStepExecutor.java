package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Individual step executor — runs a single step within a change plan.
 * Distinct from {@code ChangeExecutor} which handles the whole change.
 */
@Component
public class ChangeStepExecutor {

    public ExecutionResult executeStep(String stepName) {
        return ExecutionResult.success("ChangeStepExecutor",
                "Step '" + stepName + "' completed");
    }
}
