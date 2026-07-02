package com.example.telecom.gateway.mapper;

import com.example.telecom.gateway.dto.*;

public class DashboardResponseMapper {

    public DashboardResponse toResponse(DeviceHealthSummary deviceHealth,
                                         AlarmSummary alarmSummary,
                                         WorkOrderSummary workOrderSummary) {
        DashboardResponse response = new DashboardResponse();
        response.setDeviceHealth(deviceHealth);
        response.setAlarms(alarmSummary);
        response.setWorkOrders(workOrderSummary);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
