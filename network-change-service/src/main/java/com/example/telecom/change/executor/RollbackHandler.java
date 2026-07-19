package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.RollbackResult;

/**
 * Handler responsible for rolling back a change. Demonstrates interface
 * default method: the default {@code rollback} delegates to
 * {@code validateRollback} and {@code doRollback}, so implementations
 * need only override the building blocks.
 */
public interface RollbackHandler {

    /** Default rollback — validates then executes. */
    default RollbackResult rollback(ChangeContext context) {
        return validateRollback(context) ? doRollback(context) : RollbackResult.skipped();
    }

    /** Whether the rollback is valid for this context. */
    boolean validateRollback(ChangeContext context);

    /** Actually perform the rollback. */
    RollbackResult doRollback(ChangeContext context);

    /** Human-readable handler name for audit. */
    String handlerName();
}
