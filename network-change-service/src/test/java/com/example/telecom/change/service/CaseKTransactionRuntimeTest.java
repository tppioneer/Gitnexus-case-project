package com.example.telecom.change.service;

import com.example.telecom.change.domain.AuditCategory;
import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.entity.ChangeAuditEntity;
import com.example.telecom.change.repository.ChangeAuditJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runtime transaction oracle — exercises actual transaction behavior on H2.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class CaseKTransactionRuntimeTest {

    @Autowired private ChangePlanService planService;
    @Autowired private TransactionProxyCaller proxyCaller;
    @Autowired private ChangeAuditJpaRepository auditRepo;
    @Autowired private PlatformTransactionManager txManager;

    @Test
    void classLevelTransactional_appliesToMethodWithoutAnnotation() throws Exception {
        // ChangePlanService has class-level @Transactional
        // approve() has no method-level @Transactional → inherits class default
        Transactional classTx = ChangePlanService.class.getAnnotation(Transactional.class);
        assertNotNull(classTx, "class-level @Transactional must be present");

        Method approve = ChangePlanService.class.getMethod("approve", String.class, String.class);
        Transactional methodTx = approve.getAnnotation(Transactional.class);
        assertNull(methodTx, "approve() must not override — inherits class default");
    }

    @Test
    void readOnly_overrideOnPreview() throws Exception {
        Method m = ChangePlanService.class.getMethod("preview", String.class);
        Transactional tx = m.getAnnotation(Transactional.class);
        assertNotNull(tx);
        assertTrue(tx.readOnly(), "preview must override to readOnly=true");
    }

    @Test
    void requiresNew_onRecordInternal() throws Exception {
        Method m = ChangePlanService.class.getMethod("recordInternal", String.class, String.class, String.class);
        Transactional tx = m.getAnnotation(Transactional.class);
        assertNotNull(tx);
        assertEquals(Propagation.REQUIRES_NEW, tx.propagation());
    }

    @Test
    void rollbackFor_onCreatePlan() throws Exception {
        Method m = ChangePlanService.class.getMethod("createPlan", NetworkChangePlan.class);
        Transactional tx = m.getAnnotation(Transactional.class);
        assertNotNull(tx);
        assertTrue(tx.rollbackFor().length > 0, "createPlan must declare rollbackFor");
        assertEquals(ChangeValidationException.class, tx.rollbackFor()[0]);
    }

    @Test
    void crossBean_requiresNew_writesAudit_inSeparateTransaction() {
        // Cross-bean: planService → auditService.record() via Spring proxy
        // auditService.record has REQUIRES_NEW; via proxy it starts a new transaction.
        auditRepo.deleteAll();
        auditRepo.flush();

        planService.recordViaAuditService("CHG-TX-001", "test-action", "cross-bean");

        List<ChangeAuditEntity> found = auditRepo.findByAction("test-action");
        assertEquals(1, found.size(), "cross-bean REQUIRES_NEW must have committed");
    }

    @Test
    void externalProxyCall_requiresNew_persistsIndependently() {
        // External caller: TransactionProxyCaller → planService.recordInternal()
        // via Spring proxy → REQUIRES_NEW starts a new transaction.
        auditRepo.deleteAll();
        auditRepo.flush();

        proxyCaller.callRecordInternalExternally("CHG-TX-002", "proxy-action", "via-proxy");

        List<ChangeAuditEntity> found = auditRepo.findByAction("proxy-action");
        assertEquals(1, found.size(), "external proxy call must persist via REQUIRES_NEW");
    }

    @Test
    void changeAuditService_record_isRequiresNew() throws Exception {
        Method m = ChangeAuditService.class.getMethod("record", String.class, String.class, String.class);
        Transactional tx = m.getAnnotation(Transactional.class);
        assertNotNull(tx);
        assertEquals(Propagation.REQUIRES_NEW, tx.propagation());
    }

    /**
     * Self-invocation rollback oracle.
     *
     * {@code approveAndRecordInternally} calls {@code this.recordInternal()}, which
     * carries {@code @Transactional(propagation = REQUIRES_NEW)}. Because the call
     * goes through {@code this} rather than the Spring proxy, REQUIRES_NEW is
     * ignored and the inner call participates in the outer transaction.
     *
     * We wrap the call in an outer transaction that is rolled back afterwards.
     * If REQUIRES_NEW had been honored, the audit record would survive.
     * Since self-invocation bypasses the proxy, the record is rolled back too.
     */
    @Test
    void selfInvocation_requiresNew_rolledBackWithOuterTransaction() {
        auditRepo.deleteAll();
        auditRepo.flush();

        TransactionTemplate outerTx = new TransactionTemplate(txManager);
        outerTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        outerTx.execute(status -> {
            planService.approveAndRecordInternally("CHG-TX-SELF", "self-test");
            // Force rollback of the outer transaction
            status.setRollbackOnly();
            return null;
        });

        List<ChangeAuditEntity> found = auditRepo.findByAction("internal-approval");
        assertEquals(0, found.size(),
                "self-invocation bypassed the proxy, so REQUIRES_NEW had no effect — "
              + "the audit write was rolled back with the outer transaction");
    }

    /**
     * Cross-bean rollback oracle.
     *
     * {@code TransactionProxyCaller.callRecordInternalExternally} invokes
     * {@code planService.recordInternal()} through the Spring proxy, so
     * {@code REQUIRES_NEW} starts an independent transaction. Even when the
     * outer transaction is rolled back, the inner transaction has already
     * committed and the audit record survives.
     */
    @Test
    void crossBeanProxy_requiresNew_survivesOuterRollback() {
        auditRepo.deleteAll();
        auditRepo.flush();

        TransactionTemplate outerTx = new TransactionTemplate(txManager);
        outerTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        outerTx.execute(status -> {
            proxyCaller.callRecordInternalExternally("CHG-TX-PROXY", "proxy-action-xbean", "cross-bean rollback");
            status.setRollbackOnly();
            return null;
        });

        // The inner transaction committed independently. Querying outside the
        // rolled-back outer transaction should see the committed record.
        auditRepo.flush();
        List<ChangeAuditEntity> found = auditRepo.findByAction("proxy-action-xbean");
        assertEquals(1, found.size(),
                "cross-bean proxy call honored REQUIRES_NEW — audit record committed independently");
    }
}
