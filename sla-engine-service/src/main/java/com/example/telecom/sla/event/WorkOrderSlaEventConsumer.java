package com.example.telecom.sla.event;

import com.example.telecom.common.event.WorkOrderEventConsumer;
import com.example.telecom.common.workorder.WorkOrderEvent;
import com.example.telecom.common.workorder.WorkOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderSlaEventConsumer implements WorkOrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderSlaEventConsumer.class);

    @Override
    public void onWorkOrderChanged(WorkOrderEvent event) {
        log.info("Work order event received: workOrderId={}, from={}, to={}",
                event.getWorkOrderId(), event.getFromStatus(), event.getToStatus());

        if (event.getToStatus() == WorkOrderStatus.PROCESSING) {
            onWorkOrderCreated(event);
        } else if (event.getToStatus() == WorkOrderStatus.RESOLVED) {
            onWorkOrderCompleted(event);
        }
    }

    public void onWorkOrderCreated(WorkOrderEvent event) {
        log.info("Work order created, initiating SLA monitoring: workOrderId={}, region={}",
                event.getWorkOrderId(), event.getMaintenanceRegionCode());
    }

    public void onWorkOrderCompleted(WorkOrderEvent event) {
        log.info("Work order completed, recording SLA metrics: workOrderId={}",
                event.getWorkOrderId());
    }

    public void onWorkOrderSlaBreached(WorkOrderEvent event) {
        log.warn("Work order SLA breached: workOrderId={}, assignee={}",
                event.getWorkOrderId(), event.getAssignee());
    }
}
