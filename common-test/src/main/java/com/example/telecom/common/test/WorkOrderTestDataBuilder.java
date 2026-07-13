package com.example.telecom.common.test;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;

/**
 * Builder for creating {@link WorkOrder} instances in tests.
 *
 * <p>All fields default to sensible values so tests only need to override
 * the fields relevant to their scenario.
 */
public class WorkOrderTestDataBuilder {

    private String workOrderId = "WO-001";
    private String alarmId = "alarm-DEV-001";
    private String deviceId = "DEV-001";
    private WorkOrderStatus status = WorkOrderStatus.CREATED;
    private WorkOrderPriority priority = WorkOrderPriority.MEDIUM;
    private String title = "Investigate device DEV-001";
    private String description = "Routine investigation of reported issues on device DEV-001";
    private String maintenanceRegionCode = "REG-001";
    private String assignee = "engineer-default";
    private long createdTime = System.currentTimeMillis();

    /** Creates a new builder with default values. */
    public WorkOrderTestDataBuilder() {
    }

    /** @param workOrderId unique work-order identifier */
    public WorkOrderTestDataBuilder withWorkOrderId(final String workOrderId) {
        this.workOrderId = workOrderId;
        return this;
    }

    /** @param alarmId the alarm that triggered this work order */
    public WorkOrderTestDataBuilder withAlarmId(final String alarmId) {
        this.alarmId = alarmId;
        return this;
    }

    /** @param deviceId the device this work order relates to */
    public WorkOrderTestDataBuilder withDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    /** @param status work-order status */
    public WorkOrderTestDataBuilder withStatus(final WorkOrderStatus status) {
        this.status = status;
        return this;
    }

    /** @param priority priority level */
    public WorkOrderTestDataBuilder withPriority(final WorkOrderPriority priority) {
        this.priority = priority;
        return this;
    }

    /** @param title short work-order title */
    public WorkOrderTestDataBuilder withTitle(final String title) {
        this.title = title;
        return this;
    }

    /** @param description detailed work-order description */
    public WorkOrderTestDataBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    /** @param maintenanceRegionCode maintenance region code */
    public WorkOrderTestDataBuilder withMaintenanceRegionCode(final String maintenanceRegionCode) {
        this.maintenanceRegionCode = maintenanceRegionCode;
        return this;
    }

    /** @param assignee user assigned to the work order */
    public WorkOrderTestDataBuilder withAssignee(final String assignee) {
        this.assignee = assignee;
        return this;
    }

    /** @param createdTime epoch-millis timestamp when the work order was created */
    public WorkOrderTestDataBuilder withCreatedTime(final long createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    /**
     * Builds a single {@link WorkOrder} from the current builder state.
     *
     * @return a new WorkOrder instance
     */
    public WorkOrder build() {
        final WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(workOrderId);
        wo.setAlarmId(alarmId);
        wo.setDeviceId(deviceId);
        wo.setStatus(status);
        wo.setPriority(priority);
        wo.setTitle(title);
        wo.setDescription(description);
        wo.setMaintenanceRegionCode(maintenanceRegionCode);
        wo.setAssignee(assignee);
        wo.setCreatedTime(createdTime);
        wo.setUpdatedTime(createdTime);
        return wo;
    }

    /**
     * Creates a work order whose SLA deadline is already in the past, simulating
     * an SLA breach.
     *
     * @return a new WorkOrder instance past its SLA deadline
     */
    public WorkOrder buildWithSlaBreach() {
        this.status = WorkOrderStatus.ASSIGNED;
        this.priority = WorkOrderPriority.CRITICAL;
        this.createdTime = System.currentTimeMillis() - 86_400_000L; // 1 day ago
        return build();
    }
}
