package com.example.telecom.gateway;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.controller.*;
import com.example.telecom.gateway.dto.*;
import com.example.telecom.gateway.service.DashboardAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceAlarmWorkOrderControllersTest {

    private DeviceOverviewController deviceController;
    private AlarmOverviewController alarmController;
    private WorkOrderOverviewController workOrderController;

    @BeforeEach
    void setUp() {
        DashboardAggregationService aggregationService = new DashboardAggregationService(
                new DeviceClient(), new AlarmClient(), new WorkOrderClient());

        deviceController = new DeviceOverviewController(aggregationService);
        alarmController = new AlarmOverviewController(aggregationService);
        workOrderController = new WorkOrderOverviewController(aggregationService);
    }

    @Test
    void deviceControllerShouldReturnSummary() {
        ApiResponse<DeviceHealthSummary> response = deviceController.getDeviceSummary("EAST");
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("EAST", response.getData().getRegionCode());
    }

    @Test
    void alarmControllerShouldReturnSummary() {
        ApiResponse<AlarmSummary> response = alarmController.getAlarmSummary("WEST");
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("WEST", response.getData().getRegionCode());
    }

    @Test
    void workOrderControllerShouldReturnSummary() {
        ApiResponse<WorkOrderSummary> response = workOrderController.getWorkOrderSummary("SOUTH");
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("SOUTH", response.getData().getRegionCode());
    }

    @Test
    void controllersShouldAcceptDefaultRegion() {
        assertNotNull(deviceController.getDeviceSummary(null).getData());
        assertNotNull(alarmController.getAlarmSummary(null).getData());
        assertNotNull(workOrderController.getWorkOrderSummary(null).getData());
    }

    @Test
    void deviceHealthSummaryShouldContainRegionCode() {
        ApiResponse<DeviceHealthSummary> response = deviceController.getDeviceSummary("EAST");
        // Case C: regionCode flows through to DeviceHealthSummary
        assertEquals("EAST", response.getData().getRegionCode());
    }
}
