package com.example.telecom.workorder.policy;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;

public class WaitingVendorPolicy {

    public boolean shouldWaitForVendor(WorkOrder workOrder) {
        if (workOrder.getPriority() == WorkOrderPriority.CRITICAL) return false;
        String desc = workOrder.getDescription();
        if (desc == null) return false;
        return desc.toLowerCase().contains("vendor")
                || desc.toLowerCase().contains("hardware")
                || desc.toLowerCase().contains("rma");
    }

    public String evaluate(WorkOrder workOrder) {
        return shouldWaitForVendor(workOrder)
                ? "WAIT_VENDOR: " + workOrder.getWorkOrderId()
                : "PROCEED: " + workOrder.getWorkOrderId();
    }
}
