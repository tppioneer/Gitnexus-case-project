package com.example.telecom.workorder;

import com.example.telecom.workorder.config.WorkOrderProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderPropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        WorkOrderProperties props = new WorkOrderProperties();
        assertTrue(props.isAutoCreateEnabled());
        assertEquals("RegionBased", props.getDefaultAssignmentStrategy());
        assertEquals(60000, props.getEscalationCheckIntervalMs());
    }

    @Test
    void shouldAllowDisablingAutoCreate() {
        WorkOrderProperties props = new WorkOrderProperties();
        props.setAutoCreateEnabled(false);
        assertFalse(props.isAutoCreateEnabled());
    }

    @Test
    void shouldAllowCustomStrategy() {
        WorkOrderProperties props = new WorkOrderProperties();
        props.setDefaultAssignmentStrategy("SkillBased");
        assertEquals("SkillBased", props.getDefaultAssignmentStrategy());
    }

    @Test
    void shouldAllowCustomEscalationInterval() {
        WorkOrderProperties props = new WorkOrderProperties();
        props.setEscalationCheckIntervalMs(300000);
        assertEquals(300000, props.getEscalationCheckIntervalMs());
    }
}
