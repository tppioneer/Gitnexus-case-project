package com.example.telecom.change.event;

import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.service.ChangeExecutionService;
import com.example.telecom.change.service.ChangePlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runtime oracle for Spring Event listeners, verifying actual framework dispatch.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class ChangeEventListenerRuntimeTest {

    @Autowired private ChangePlanService planService;
    @Autowired private ChangeExecutionService executionService;
    @Autowired private ChangeApprovedListener approvedListener;
    @Autowired private ChangeCompletedListener completedListener;
    @Autowired private ChangeNotificationListener notificationListener;
    @Autowired private TransactionTemplate transactionTemplate;

    @Test
    void approvedListener_receivesEvent_viaPublisher() throws Exception {
        approvedListener.clear();

        NetworkChangePlan plan = new NetworkChangePlan("CHG-EVTR-001", "Runtime Event Test",
                "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        planService.createPlan(plan);
        planService.approve("CHG-EVTR-001", "approver-1");

        assertEquals(1, approvedListener.count(),
                "Listener must receive exactly one ChangeApprovedEvent");
        assertEquals("CHG-EVTR-001", approvedListener.received().get(0).getPlanId());
        assertTrue(approvedListener.received().get(0).isApproved());
    }

    @Test
    void completedListener_afterCommit_firesOnlyAfterTransactionCommits() throws Exception {
        completedListener.clear();

        // Use TransactionTemplate to wrap the event publication in a real transaction.
        // AFTER_COMMIT listener should fire only after commit.
        transactionTemplate.execute(status -> {
            executionService.complete("CHG-EVTR-002", true);
            return null;
        });

        // After the transaction commits, the AFTER_COMMIT listener should have fired.
        assertEquals(1, completedListener.count(),
                "AFTER_COMMIT listener must fire once after transaction commits");
        assertEquals("CHG-EVTR-002", completedListener.received().get(0).getPlanId());
    }

    @Test
    void completedListener_afterCommit_doesNotFireOnRollback() throws Exception {
        completedListener.clear();

        // Force a rollback — AFTER_COMMIT must not fire.
        try {
            transactionTemplate.execute(status -> {
                executionService.complete("CHG-EVTR-003", true);
                status.setRollbackOnly();
                return null;
            });
        } catch (RuntimeException ignored) {
            // setRollbackOnly doesn't throw, but the transaction is rolled back
        }

        assertEquals(0, completedListener.count(),
                "AFTER_COMMIT listener must NOT fire when transaction is rolled back");
    }

    @Test
    void notificationListener_async_receivesViaLatch() throws Exception {
        notificationListener.clear();
        notificationListener.resetLatch(1);

        // Publish event in a transaction
        transactionTemplate.execute(status -> {
            executionService.complete("CHG-EVTR-004", true);
            return null;
        });

        // Wait for async notification with deterministic latch
        boolean received = notificationListener.awaitNotification(5, TimeUnit.SECONDS);
        assertTrue(received, "Async listener must fire within timeout");
        assertTrue(notificationListener.count() >= 1,
                "Async listener must have received at least one event");
    }
}
