package com.example.telecom.notification;

import com.example.telecom.notification.config.NotificationProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationPropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        NotificationProperties props = new NotificationProperties();
        assertEquals(3, props.getMaxRetries());
        assertEquals(5000, props.getRetryDelayMs());
        assertTrue(props.isSmsEnabled());
        assertTrue(props.isEmailEnabled());
        assertTrue(props.isWecomEnabled());
    }

    @Test
    void shouldAllowDisablingChannels() {
        NotificationProperties props = new NotificationProperties();
        props.setSmsEnabled(false);
        props.setEmailEnabled(false);
        props.setWecomEnabled(false);

        assertFalse(props.isSmsEnabled());
        assertFalse(props.isEmailEnabled());
        assertFalse(props.isWecomEnabled());
    }

    @Test
    void shouldAllowCustomRetrySettings() {
        NotificationProperties props = new NotificationProperties();
        props.setMaxRetries(5);
        props.setRetryDelayMs(10000);

        assertEquals(5, props.getMaxRetries());
        assertEquals(10000, props.getRetryDelayMs());
    }
}
