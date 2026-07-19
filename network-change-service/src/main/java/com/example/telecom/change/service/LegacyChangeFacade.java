package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;
import com.example.telecom.change.executor.ChangeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Legacy facade using field injection — demonstrates the deprecated
 * @Autowired field-injection style. Only this class uses field injection.
 */
@Component
public class LegacyChangeFacade {

    @Autowired
    @Qualifier("routerExecutor")
    private ChangeExecutor legacyExecutor;

    public ExecutionResult executeLegacy(ChangeContext context) {
        return legacyExecutor.execute(context);
    }

    public Class<?> resolvedClass() {
        return legacyExecutor.getClass();
    }
}
