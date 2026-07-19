package com.example.telecom.change.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.example.telecom.change.domain.ChangeStatus;

/**
 * JPA entity representing a single step within a network change execution.
 * Steps are ordered children of {@code NetworkChangeEntity}.
 */
@Entity
@Table(name = "change_step")
public class ChangeStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Column(name = "executor_bean")
    private String executorBean;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChangeStatus status;

    @Column(name = "result_message")
    private String resultMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")
    private NetworkChangeEntity change;

    public ChangeStepEntity() {}

    public ChangeStepEntity(int stepOrder, String stepName, String executorBean) {
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.executorBean = executorBean;
        this.status = ChangeStatus.DRAFT;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    public String getExecutorBean() { return executorBean; }
    public void setExecutorBean(String executorBean) { this.executorBean = executorBean; }
    public ChangeStatus getStatus() { return status; }
    public void setStatus(ChangeStatus status) { this.status = status; }
    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
    public NetworkChangeEntity getChange() { return change; }
    public void setChange(NetworkChangeEntity change) { this.change = change; }
}
