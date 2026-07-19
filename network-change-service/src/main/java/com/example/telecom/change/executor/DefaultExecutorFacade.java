package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;
import org.springframework.stereotype.Component;

/**
 * Facade that receives a bare (unqualified) {@code ChangeExecutor} injection.
 * Because {@code RadioChangeExecutor} is marked {@code @Primary}, this
 * constructor parameter is resolved to the radio executor.
 *
 * This injection path is intentionally separate from the registry path
 * to verify that @Primary selection works independently.
 */
@Component
public class DefaultExecutorFacade {

    private final ChangeExecutor defaultExecutor;

    public DefaultExecutorFacade(ChangeExecutor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }

    public ExecutionResult executeDefault(ChangeContext context) {
        return defaultExecutor.execute(context);
    }

    /** Return the resolved bean class — used in tests to verify @Primary behavior. */
    public Class<?> resolvedExecutorClass() {
        return defaultExecutor.getClass();
    }
}
