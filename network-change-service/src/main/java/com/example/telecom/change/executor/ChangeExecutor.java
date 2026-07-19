package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;

/**
 * Core interface for change executors. Each implementation handles one
 * {@code DeviceFamily}. Dispatch is dynamic — at runtime, the correct
 * implementation is selected based on the context's device family.
 */
public interface ChangeExecutor {
    /** The device family this executor handles. */
    DeviceFamily supports();

    /** Execute the change described by the given context. */
    ExecutionResult execute(ChangeContext context);
}
