package com.example.telecom.workorder.mapper;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.workorder.dto.WorkOrderResponse;

public class WorkOrderMapper {

    public WorkOrderResponse toResponse(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setWorkOrderId(workOrder.getWorkOrderId());
        response.setAlarmId(workOrder.getAlarmId());
        response.setDeviceId(workOrder.getDeviceId());
        response.setStatus(workOrder.getStatus().name());
        response.setPriority(workOrder.getPriority().name());
        response.setTitle(workOrder.getTitle());
        response.setDescription(workOrder.getDescription());
        response.setMaintenanceRegionCode(workOrder.getMaintenanceRegionCode());
        response.setAssignee(workOrder.getAssignee());
        response.setCreatedTime(workOrder.getCreatedTime());
        response.setUpdatedTime(workOrder.getUpdatedTime());
        return response;
    }
}
