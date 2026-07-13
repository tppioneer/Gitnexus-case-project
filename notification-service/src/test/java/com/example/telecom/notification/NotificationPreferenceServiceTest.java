package com.example.telecom.notification;

import com.example.telecom.common.user.NotificationPreference;
import com.example.telecom.notification.repository.NotificationPreferenceRepository;
import com.example.telecom.notification.service.NotificationPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationPreferenceServiceTest {

    private NotificationPreferenceRepository preferenceRepository;
    private NotificationPreferenceService preferenceService;

    @BeforeEach
    void setUp() {
        preferenceRepository = new NotificationPreferenceRepository();
        preferenceService = new NotificationPreferenceService(preferenceRepository);
    }

    @Test
    void shouldCreateAndRetrievePreference() {
        NotificationPreference preference = new NotificationPreference(
                "user-1", List.of("SMS", "EMAIL", "WECOM")
        );

        NotificationPreference saved = preferenceService.createOrUpdatePreference(preference);
        assertNotNull(saved);
        assertEquals("user-1", saved.getUserId());
        assertTrue(saved.getPreferredChannels().contains("SMS"));

        NotificationPreference retrieved = preferenceService.getPreference("user-1");
        assertNotNull(retrieved);
        assertEquals("user-1", retrieved.getUserId());
        assertEquals(3, retrieved.getPreferredChannels().size());
    }

    @Test
    void shouldUpdateChannelEnabled() {
        NotificationPreference preference = new NotificationPreference(
                "user-2", List.of("SMS", "EMAIL")
        );
        preferenceService.createOrUpdatePreference(preference);

        preferenceService.updateChannelEnabled("user-2", Map.of("SMS", false, "EMAIL", true));

        NotificationPreference updated = preferenceService.getPreference("user-2");
        assertFalse(updated.isChannelEnabled("SMS"));
        assertTrue(updated.isChannelEnabled("EMAIL"));
    }

    @Test
    void shouldThrowWhenPreferenceNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> preferenceService.getPreference("non-existent-user"));
    }

    @Test
    void shouldUpdateQuietHours() {
        NotificationPreference preference = new NotificationPreference(
                "user-3", List.of("SMS")
        );
        preferenceService.createOrUpdatePreference(preference);

        preferenceService.updateQuietHours("user-3", 22, 7);

        NotificationPreference updated = preferenceService.getPreference("user-3");
        assertTrue(updated.isQuietHoursEnabled());
    }

    @Test
    void shouldDeletePreference() {
        NotificationPreference preference = new NotificationPreference(
                "user-4", List.of("EMAIL")
        );
        preferenceService.createOrUpdatePreference(preference);

        preferenceService.deletePreference("user-4");
        assertThrows(IllegalArgumentException.class,
                () -> preferenceService.getPreference("user-4"));
    }

    @Test
    void shouldGetAllPreferences() {
        preferenceService.createOrUpdatePreference(new NotificationPreference("u1", List.of("SMS")));
        preferenceService.createOrUpdatePreference(new NotificationPreference("u2", List.of("EMAIL")));

        List<NotificationPreference> all = preferenceService.getAllPreferences();
        assertEquals(2, all.size());
    }

    @Test
    void shouldCheckChannelAllowed() {
        NotificationPreference preference = new NotificationPreference(
                "user-5", List.of("SMS", "EMAIL")
        );
        preferenceService.createOrUpdatePreference(preference);

        boolean allowed = preferenceService.isChannelAllowed("user-5", "SMS");
        assertTrue(allowed);
    }
}
