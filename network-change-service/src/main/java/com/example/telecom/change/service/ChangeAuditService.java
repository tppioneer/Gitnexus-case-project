package com.example.telecom.change.service;

import com.example.telecom.change.annotation.AuditOperation;
import com.example.telecom.change.domain.AuditCategory;
import com.example.telecom.change.entity.ChangeAuditEntity;
import com.example.telecom.change.repository.ChangeAuditJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Audit service — records audit entries in a REQUIRES_NEW transaction.
 * When called via a Spring proxy, each call gets its own transaction.
 * When called via self-invocation (see ChangePlanService.approveAndRecordInternally),
 * the REQUIRES_NEW semantics are lost.
 */
@Service
public class ChangeAuditService {

    private final ChangeAuditJpaRepository auditRepository;

    public ChangeAuditService(ChangeAuditJpaRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @AuditOperation(action = "record-audit", category = AuditCategory.CHANGE)
    public void record(String planId, String action, String details) {
        ChangeAuditEntity entity = new ChangeAuditEntity(
                action, AuditCategory.CHANGE, "system", details, true);
        auditRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<ChangeAuditEntity> findByAction(String action) {
        return auditRepository.findByAction(action);
    }

    /**
     * Called as a method-reference callback by {@code ChangeCallbackRegistry}
     * when a change reaches a terminal status. Records an audit entry with
     * the distinctive action {@code callback-change-completed} so that the
     * callback invocation is distinguishable from any direct audit writes.
     */
    public void onCompleted(String planId) {
        record(planId, "callback-change-completed", "Change " + planId + " completed via callback");
    }
}
