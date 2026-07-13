package com.example.telecom.workorder.service;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Automatically creates work orders from critical/major alarm events.
 * EXPLICITLY maps AlarmEvent.deviceRegionCode → WorkOrder.maintenanceRegionCode.
 * This is a key node in Case A flow tracing.
 */
public class AutoWorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderFlowService workOrderFlowService;

    public AutoWorkOrderService(WorkOrderRepository workOrderRepository,
                                 WorkOrderFlowService workOrderFlowService) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderFlowService = workOrderFlowService;
    }

    public WorkOrder createForAlarm(AlarmEvent event) {
        // Only auto-create work orders for critical and major alarms
        if (event.getSeverity() != Severity.CRITICAL && event.getSeverity() != Severity.MAJOR) {
            return null;
        }

        WorkOrder workOrder = new WorkOrder(
                UUID.randomUUID().toString(),
                event.getAlarmId(),
                event.getDeviceId(),
                WorkOrderStatus.CREATED,
                mapPriority(event.getSeverity()),
                "Auto-created from alarm " + event.getAlarmId(),
                "Alarm triggered for device " + event.getDeviceId(),
                event.getDeviceRegionCode(),  // ← EXPLICIT: deviceRegionCode → maintenanceRegionCode
                System.currentTimeMillis()
        );

        workOrderRepository.save(workOrder);
        workOrderFlowService.assign(workOrder);
        return workOrder;
    }

    private WorkOrderPriority mapPriority(Severity severity) {
        return switch (severity) {
            case CRITICAL -> WorkOrderPriority.CRITICAL;
            case MAJOR -> WorkOrderPriority.HIGH;
            case WARNING -> WorkOrderPriority.MEDIUM;
            case INFO -> WorkOrderPriority.LOW;
        };
    }

    /**
     * Create work orders for multiple alarm events. Only critical and major alarms trigger creation.
     */
    public List<WorkOrder> createForMultipleAlarms(List<AlarmEvent> events) {
        List<WorkOrder> result = new ArrayList<>();
        for (AlarmEvent event : events) {
            if (event.getSeverity() == Severity.CRITICAL || event.getSeverity() == Severity.MAJOR) {
                result.add(createForAlarm(event));
            }
        }
        return result;
    }

    /**
     * Create a work order for an alarm only if the event's severity is at least the given threshold.
     */
    public WorkOrder createForSeverity(AlarmEvent event, Severity severity) {
        if (event.getSeverity().ordinal() >= severity.ordinal()) {
            return createForAlarm(event);
        }
        return null;
    }

    /**
     * Check whether an alarm event should trigger automatic work order creation.
     */
    public boolean shouldAutoCreate(AlarmEvent event) {
        return event.getSeverity() == Severity.CRITICAL || event.getSeverity() == Severity.MAJOR;
    }

    /**
     * Create work orders in bulk for alarm events filtered by a specific region code.
     */
    public List<WorkOrder> createBulk(List<AlarmEvent> events, String regionCode) {
        List<WorkOrder> result = new ArrayList<>();
        for (AlarmEvent event : events) {
            if ((event.getSeverity() == Severity.CRITICAL || event.getSeverity() == Severity.MAJOR)
                    && regionCode.equals(event.getDeviceRegionCode())) {
                result.add(createForAlarm(event));
            }
        }
        return result;
    }
}
