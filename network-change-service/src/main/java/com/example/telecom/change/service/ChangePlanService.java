package com.example.telecom.change.service;

import com.example.telecom.change.annotation.AuditOperation;
import com.example.telecom.change.annotation.ChangeGuard;
import com.example.telecom.change.annotation.RequiredCapability;
import com.example.telecom.change.domain.AuditCategory;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.event.ChangeApprovedEvent;
import com.example.telecom.change.repository.ChangeSnapshotStore;
import com.example.telecom.change.entity.ChangeAuditEntity;
import com.example.telecom.change.repository.ChangeAuditJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core service for creating and managing change plans.
 *
 * Carries class-level @Transactional as the default; individual methods
 * may override with readOnly, propagation, or rollbackFor.
 */
@Service
@ChangeGuard(risk = ChangeRisk.LOW)
@RequiredCapability("change.write")
@RequiredCapability("audit.log")
@Transactional
public class ChangePlanService {

    private final ChangeSnapshotStore snapshotStore;
    private final ApplicationEventPublisher eventPublisher;
    private final ChangeAuditService auditService;
    private final ChangeAuditJpaRepository auditRepository;

    public ChangePlanService(ChangeSnapshotStore snapshotStore,
                             ApplicationEventPublisher eventPublisher,
                             ChangeAuditService auditService,
                             ChangeAuditJpaRepository auditRepository) {
        this.snapshotStore = snapshotStore;
        this.eventPublisher = eventPublisher;
        this.auditService = auditService;
        this.auditRepository = auditRepository;
    }

    /** Write transaction; explicit rollbackFor a checked exception. */
    @Transactional(rollbackFor = ChangeValidationException.class)
    @AuditOperation(action = "create-plan", category = AuditCategory.CHANGE)
    public NetworkChangePlan createPlan(NetworkChangePlan plan) throws ChangeValidationException {
        if (plan.getPlanId() == null || plan.getPlanId().isBlank()) {
            throw new ChangeValidationException("planId required");
        }
        snapshotStore.save(plan);
        return plan;
    }

    /** Read-only override of the class-level default. */
    @Transactional(readOnly = true)
    public NetworkChangePlan preview(String planId) {
        return snapshotStore.findByPlanId(planId).orElse(null);
    }

    /** Composed annotation audit + event publication. */
    @com.example.telecom.change.annotation.CriticalChange
    public void approve(String planId, String approverId) {
        snapshotStore.findByPlanId(planId).ifPresent(plan -> {
            plan.setStatus(ChangeStatus.APPROVED);
            snapshotStore.save(plan);
            eventPublisher.publishEvent(new ChangeApprovedEvent(planId, approverId, true));
        });
    }

    /**
     * Approves a plan and records an internal audit entry. Note that
     * {@code recordInternal} is called via {@code this.}, which means the
     * call stays within the same bean instance and does not traverse the
     * Spring transaction proxy.
     */
    public void approveAndRecordInternally(String planId, String approverId) {
        approve(planId, approverId);
        this.recordInternal(planId, "internal-approval", "self-invocation audit");
    }

    /**
     * Records an audit entry with a dedicated REQUIRES_NEW transaction
     * when invoked through the Spring proxy.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordInternal(String planId, String action, String details) {
        ChangeAuditEntity entity = new ChangeAuditEntity(
                action, AuditCategory.CHANGE, "system", details, true);
        auditRepository.save(entity);
    }

    /** Cross-bean call to ChangeAuditService.record — runs in a separate REQUIRES_NEW transaction. */
    public void recordViaAuditService(String planId, String action, String details) {
        auditService.record(planId, action, details);
    }

    /** Fully-qualified form of the same annotation type. */
    @com.example.telecom.change.annotation.AuditOperation(
            action = "update-risk",
            category = AuditCategory.CHANGE,
            sensitive = true
    )
    public void updateRisk(String planId, ChangeRisk newRisk) {
        snapshotStore.findByPlanId(planId).ifPresent(plan -> {
            plan.setRisk(newRisk);
            snapshotStore.save(plan);
        });
    }
}
