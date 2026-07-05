package com.example.telecom.common;

import com.example.telecom.common.alarm.*;
import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.api.PagedResult;
import com.example.telecom.common.audit.AuditEntry;
import com.example.telecom.common.device.*;
import com.example.telecom.common.event.*;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.common.region.Region;
import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive construction tests for all domain objects.
 * Verifies constructors, getters, and setters work correctly.
 */
class DomainObjectConstructionTest {

    @Test
    void shouldConstructDeviceInfo() {
        DeviceInfo di = new DeviceInfo("d1", "name", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.0.1", true);
        assertEquals("d1", di.getDeviceId());
        assertEquals("EAST", di.getMaintenanceRegionCode());
        assertTrue(di.isActive());
    }

    @Test
    void shouldConstructDeviceMetric() {
        Instant now = Instant.now();
        DeviceMetric dm = new DeviceMetric("m1", "d1", MetricType.CPU_USAGE,
                50.0, "%", now, "Huawei");
        assertEquals("m1", dm.getMetricId());
        assertEquals(50.0, dm.getValue());
        assertFalse(dm.isNormalized());
    }

    @Test
    void shouldConstructDeviceMetricEvent() {
        DeviceMetricEvent event = new DeviceMetricEvent("e1", "d1", "m1", "CPU_USAGE",
                90.0, "%", 1000L, "EAST");
        assertEquals("e1", event.getEventId());
        assertEquals("EAST", event.getDeviceRegionCode());
    }

    @Test
    void shouldConstructAlarmRecord() {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "SOUTH", 2000L);
        assertEquals("a1", alarm.getAlarmId());
        assertEquals("SOUTH", alarm.getAlarmRegionCode());
        assertEquals(Severity.CRITICAL, alarm.getSeverity());
    }

    @Test
    void shouldConstructAlarmEvent() {
        AlarmEvent event = new AlarmEvent("e1", "a1", "d1", Severity.MAJOR,
                AlarmStatus.OPEN, "WEST", 3000L);
        assertEquals("WEST", event.getDeviceRegionCode());
    }

    @Test
    void shouldConstructThresholdRule() {
        ThresholdRule rule = new ThresholdRule("r1", "CPU Rule", "CPU_USAGE",
                80.0, Severity.MAJOR, true);
        assertTrue(rule.isEnabled());
        assertEquals("CPU_USAGE", rule.getMetricType());
    }

    @Test
    void shouldConstructEvaluationResult() {
        EvaluationResult result = new EvaluationResult(true, "r1", "m1", 90.0, 80.0,
                Severity.CRITICAL, "CPU high");
        assertTrue(result.isTriggered());
        assertEquals(90.0, result.getCurrentValue());
    }

    @Test
    void shouldConstructEvaluationContext() {
        EvaluationContext ctx = new EvaluationContext("d1", "EAST");
        assertEquals("d1", ctx.getDeviceId());
        assertEquals("EAST", ctx.getDeviceRegionCode());
    }

    @Test
    void shouldConstructWorkOrder() {
        WorkOrder wo = new WorkOrder("w1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "title", "desc", "NORTH", 4000L);
        assertEquals(WorkOrderStatus.CREATED, wo.getStatus());
        assertEquals("NORTH", wo.getMaintenanceRegionCode());
    }

    @Test
    void shouldConstructWorkOrderEvent() {
        WorkOrderEvent event = new WorkOrderEvent("e1", "w1", WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED, "op-1", "EAST", 5000L);
        assertEquals("EAST", event.getMaintenanceRegionCode());
    }

    @Test
    void shouldConstructRegion() {
        Region region = new Region("r1", "East", "EAST", null);
        assertEquals("EAST", region.getRegionCode());
    }

    @Test
    void shouldConstructOperatorUser() {
        OperatorUser user = new OperatorUser("u1", "Alice", "NORTH", "111", "a@t.com", "expert");
        assertEquals("NORTH", user.getRegionCode());
    }

    @Test
    void shouldConstructApiResponse() {
        ApiResponse<String> response = ApiResponse.success("data");
        assertEquals(200, response.getCode());
        assertEquals("data", response.getData());

        ApiResponse<String> error = ApiResponse.error(500, "error");
        assertEquals(500, error.getCode());
        assertNull(error.getData());
    }

    @Test
    void shouldConstructPagedResult() {
        PagedResult<String> paged = new PagedResult<>(List.of("a", "b"), 1, 10, 25);
        assertEquals(2, paged.getItems().size());
        assertEquals(3, paged.getTotalPages());
        assertEquals(25, paged.getTotal());
    }

    @Test
    void shouldConstructAuditEntry() {
        AuditEntry entry = new AuditEntry("a1", "Device", "d1", "CREATE",
                "system", "details", 6000L);
        assertEquals("Device", entry.getEntityType());
        assertEquals("CREATE", entry.getAction());
    }

    @Test
    void shouldConstructDomainException() {
        DomainException ex = new DomainException("ERR_001", "Something went wrong");
        assertEquals("ERR_001", ex.getErrorCode());
        assertEquals("Something went wrong", ex.getMessage());
    }

    @Test
    void shouldConstructValidationException() {
        ValidationException ex = new ValidationException("fieldName", "Invalid value");
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
        assertEquals("fieldName", ex.getFieldName());
    }
}
