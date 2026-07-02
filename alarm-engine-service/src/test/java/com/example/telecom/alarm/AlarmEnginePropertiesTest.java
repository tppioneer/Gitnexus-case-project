package com.example.telecom.alarm;

import com.example.telecom.alarm.config.AlarmEngineProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmEnginePropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        AlarmEngineProperties props = new AlarmEngineProperties();
        assertTrue(props.isDedupEnabled());
        assertTrue(props.isCorrelationEnabled());
        assertEquals(100, props.getMaxAlarmsPerDevice());
    }

    @Test
    void shouldAllowDisablingDedup() {
        AlarmEngineProperties props = new AlarmEngineProperties();
        props.setDedupEnabled(false);
        assertFalse(props.isDedupEnabled());
    }

    @Test
    void shouldAllowDisablingCorrelation() {
        AlarmEngineProperties props = new AlarmEngineProperties();
        props.setCorrelationEnabled(false);
        assertFalse(props.isCorrelationEnabled());
    }

    @Test
    void shouldAllowCustomMaxAlarms() {
        AlarmEngineProperties props = new AlarmEngineProperties();
        props.setMaxAlarmsPerDevice(500);
        assertEquals(500, props.getMaxAlarmsPerDevice());
    }
}
