package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.RollbackResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Remote rollback handler — activated only when
 * {@code telecom.change.remote.enabled=true}. Overrides the default
 * {@code rollback} method to demonstrate override-vs-default dispatch.
 */
@Component
@ConditionalOnProperty(name = "telecom.change.remote.enabled", havingValue = "true")
public class RemoteRollbackHandler implements RollbackHandler {

    @Override
    public boolean validateRollback(ChangeContext context) {
        return context.getChangeId() != null && context.getRegionCode() != null;
    }

    @Override
    public RollbackResult doRollback(ChangeContext context) {
        return RollbackResult.applied(handlerName(),
                "Remote rollback for " + context.getChangeId() + " in " + context.getRegionCode());
    }

    /** Override the default rollback method — exercises a different code path. */
    @Override
    public RollbackResult rollback(ChangeContext context) {
        if (!validateRollback(context)) {
            return RollbackResult.skipped();
        }
        RollbackResult result = doRollback(context);
        return new RollbackResult(result.isApplied(),
                result.getMessage() + " [remote]", handlerName());
    }

    @Override
    public String handlerName() {
        return "RemoteRollbackHandler";
    }
}
