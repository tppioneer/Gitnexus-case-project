package com.example.telecom.collector;

import com.example.telecom.collector.mapper.DeviceMetricMapper;
import com.example.telecom.common.device.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMetricMapperTest {

    @Test
    void shouldMapRegionCodeToDeviceRegionCodeInEvent() {
        DeviceMetricMapper mapper = new DeviceMetricMapper();
        DeviceMetric metric = new DeviceMetric("m1", "dev-1", MetricType.CPU_USAGE, 50.0,
                "%", Instant.now(), "Huawei");
        DeviceInfo deviceInfo = new DeviceInfo("dev-1", "BS-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-1", "10.0.0.1", true);

        DeviceMetricEvent event = mapper.toEvent(metric, deviceInfo);

        // Case C: DeviceInfo.maintenanceRegionCode → DeviceMetricEvent.deviceRegionCode
        assertEquals("EAST", event.getDeviceRegionCode());
        assertEquals(deviceInfo.getMaintenanceRegionCode(), event.getDeviceRegionCode());
    }
}
