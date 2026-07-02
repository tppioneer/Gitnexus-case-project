package com.example.telecom.gateway;

import com.example.telecom.gateway.config.OpsGatewayProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpsGatewayPropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        OpsGatewayProperties props = new OpsGatewayProperties();
        assertEquals(60, props.getCacheTtlSeconds());
        assertEquals(100, props.getMaxDashboardItems());
        assertEquals("all", props.getDefaultRegion());
    }

    @Test
    void shouldAllowCustomCacheTtl() {
        OpsGatewayProperties props = new OpsGatewayProperties();
        props.setCacheTtlSeconds(120);
        assertEquals(120, props.getCacheTtlSeconds());
    }

    @Test
    void shouldAllowCustomMaxItems() {
        OpsGatewayProperties props = new OpsGatewayProperties();
        props.setMaxDashboardItems(500);
        assertEquals(500, props.getMaxDashboardItems());
    }

    @Test
    void shouldAllowCustomDefaultRegion() {
        OpsGatewayProperties props = new OpsGatewayProperties();
        props.setDefaultRegion("EAST");
        assertEquals("EAST", props.getDefaultRegion());
    }
}
