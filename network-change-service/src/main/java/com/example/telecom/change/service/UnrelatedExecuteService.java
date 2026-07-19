package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Service with an {@code execute(ChangeContext)} method that has the same
 * name and signature as {@code ChangeExecutor.execute}, but does not
 * implement the {@code ChangeExecutor} interface.
 */
@Component
public class UnrelatedExecuteService {

    /**
     * Has the same name and signature as {@code ChangeExecutor.execute},
     * but is unrelated to that interface — this class does not implement it.
     */
    public ExecutionResult execute(ChangeContext context) {
        return ExecutionResult.success("UnrelatedExecuteService",
                "Unrelated execution of " + context.getChangeId());
    }
}
