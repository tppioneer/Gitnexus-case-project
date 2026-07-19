package com.example.telecom.change.entity;

import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.domain.DeviceFamily;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Network change record — the central JPA entity.
 *
 * Most fields use the default field access (the {@code @Id} on a field
 * sets the entity's default to field access). The {@code title} field
 * is the exception: it uses property access, with all mapping annotations
 * placed on the getter.
 *
 * The DTO class {@code ChangeRecordDto} looks superficially similar but has
 * no {@code @Entity} annotation — it is a negative example.
 */
@Entity
@Table(name = "network_change")
@NamedQuery(name = "NetworkChangeEntity.findByTitle",
            query = "SELECT c FROM NetworkChangeEntity c WHERE c.title = :title")
@NamedQuery(name = "NetworkChangeEntity.findActiveByRegion",
            query = "SELECT c FROM NetworkChangeEntity c WHERE c.regionCode = :region AND c.status IN ('APPROVED', 'RUNNING')")
@NamedNativeQuery(name = "NetworkChangeEntity.findByFamilyNative",
                  query = "SELECT * FROM network_change WHERE device_family = :family",
                  resultClass = NetworkChangeEntity.class)
public class NetworkChangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "change_id", nullable = false, unique = true)
    private String changeId;

    /**
     * This field uses property access. No JPA mapping annotations are
     * placed here — they live on the getter, as required by JPA when
     * the entity default is field access but one property overrides
     * to property access.
     */
    private String title;

    @Column(name = "region_code")
    private String regionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_family")
    private DeviceFamily deviceFamily;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private ChangeRisk risk;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChangeStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Embedded
    private ChangeWindowEmbeddable maintenanceWindow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id")
    private ChangeApprovalEntity approval;

    @OneToMany(mappedBy = "change", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeStepEntity> steps = new ArrayList<>();

    @OneToMany(mappedBy = "change", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeAuditEntity> auditRecords = new ArrayList<>();

    @Version
    private Long version;

    public NetworkChangeEntity() {}

    public NetworkChangeEntity(String changeId, String title, String regionCode,
                               DeviceFamily deviceFamily, ChangeRisk risk) {
        this.changeId = changeId;
        this.title = title;
        this.regionCode = regionCode;
        this.deviceFamily = deviceFamily;
        this.risk = risk;
        this.status = ChangeStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Property-accessed mapping for {@code title}. The {@code @Access(PROPERTY)}
     * annotation tells JPA to use this getter (rather than direct field access)
     * for this property. The {@code @Column} annotation is also on the getter,
     * which is required when the entity default is field access.
     */
    @Access(AccessType.PROPERTY)
    @Column(name = "title", nullable = false)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getChangeId() { return changeId; }
    public void setChangeId(String changeId) { this.changeId = changeId; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public DeviceFamily getDeviceFamily() { return deviceFamily; }
    public void setDeviceFamily(DeviceFamily deviceFamily) { this.deviceFamily = deviceFamily; }
    public ChangeRisk getRisk() { return risk; }
    public void setRisk(ChangeRisk risk) { this.risk = risk; }
    public ChangeStatus getStatus() { return status; }
    public void setStatus(ChangeStatus status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public ChangeWindowEmbeddable getMaintenanceWindow() { return maintenanceWindow; }
    public void setMaintenanceWindow(ChangeWindowEmbeddable maintenanceWindow) { this.maintenanceWindow = maintenanceWindow; }
    public ChangeApprovalEntity getApproval() { return approval; }
    public void setApproval(ChangeApprovalEntity approval) { this.approval = approval; }
    public List<ChangeStepEntity> getSteps() { return steps; }
    public void setSteps(List<ChangeStepEntity> steps) { this.steps = steps; }
    public List<ChangeAuditEntity> getAuditRecords() { return auditRecords; }
    public void setAuditRecords(List<ChangeAuditEntity> auditRecords) { this.auditRecords = auditRecords; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public void addStep(ChangeStepEntity step) {
        steps.add(step);
        step.setChange(this);
    }

    public void addAuditRecord(ChangeAuditEntity audit) {
        auditRecords.add(audit);
        audit.setChange(this);
    }
}
