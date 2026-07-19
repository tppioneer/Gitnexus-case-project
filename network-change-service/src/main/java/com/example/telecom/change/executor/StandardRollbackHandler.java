package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.RollbackResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Standard rollback handler — active unless remote execution is enabled.
 * When {@code telecom.change.remote.enabled=true}, this bean is suppressed
 * in favor of {@code RemoteRollbackHandler}, so there is exactly one
 * {@code RollbackHandler} bean under any configuration.
 */
@Component
@ConditionalOnProperty(name = "telecom.change.remote.enabled", havingValue = "false", matchIfMissing = true)
public class StandardRollbackHandler implements RollbackHandler {

    @Override
    public boolean validateRollback(ChangeContext context) {
        return context.getChangeId() != null && !context.getChangeId().isBlank();
    }

    @Override
    public RollbackResult doRollback(ChangeContext context) {
        return RollbackResult.applied(handlerName(),
                "Standard rollback applied for " + context.getChangeId());
    }

    @Override
    public String handlerName() {
        return "StandardRollbackHandler";
    }
}
