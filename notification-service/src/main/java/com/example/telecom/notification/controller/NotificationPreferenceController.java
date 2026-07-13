package com.example.telecom.notification.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.user.NotificationPreference;
import com.example.telecom.notification.service.NotificationPreferenceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    public NotificationPreferenceController(NotificationPreferenceService notificationPreferenceService) {
        this.notificationPreferenceService = notificationPreferenceService;
    }

    @GetMapping("/{userId}")
    public ApiResponse<NotificationPreference> getPreference(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.error(400, "User ID must not be empty");
        }
        try {
            NotificationPreference result = notificationPreferenceService.getPreference(userId);
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<NotificationPreference> createOrUpdatePreference(
            @RequestBody NotificationPreference preference) {
        if (preference == null) {
            return ApiResponse.error(400, "Preference body must not be null");
        }
        if (preference.getUserId() == null || preference.getUserId().trim().isEmpty()) {
            return ApiResponse.error(400, "User ID in preference must not be empty");
        }
        NotificationPreference result = notificationPreferenceService.createOrUpdatePreference(preference);
        return ApiResponse.success(result);
    }

    @PutMapping("/{userId}/channels")
    public ApiResponse<Void> updateChannelEnabled(@PathVariable String userId,
                                                   @RequestBody Map<String, Boolean> channelMap) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.error(400, "User ID must not be empty");
        }
        if (channelMap == null || channelMap.isEmpty()) {
            return ApiResponse.error(400, "Channel map must not be empty");
        }
        try {
            notificationPreferenceService.updateChannelEnabled(userId, channelMap);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PutMapping("/{userId}/quiet-hours")
    public ApiResponse<Void> updateQuietHours(@PathVariable String userId,
                                               @RequestBody Map<String, Integer> quietHours) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.error(400, "User ID must not be empty");
        }
        Integer startHour = quietHours.get("startHour");
        Integer endHour = quietHours.get("endHour");
        if (startHour == null || endHour == null) {
            return ApiResponse.error(400, "Both startHour and endHour must be provided");
        }
        try {
            notificationPreferenceService.updateQuietHours(userId, startHour, endHour);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{userId}/quiet-hours/disable")
    public ApiResponse<Void> disableQuietHours(@PathVariable String userId) {
        try {
            notificationPreferenceService.disableQuietHours(userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deletePreference(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ApiResponse.error(400, "User ID must not be empty");
        }
        try {
            notificationPreferenceService.deletePreference(userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<NotificationPreference>> getAllPreferences() {
        List<NotificationPreference> result = notificationPreferenceService.getAllPreferences();
        return ApiResponse.success(result);
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        long total = notificationPreferenceService.getTotalPreferences();
        return ApiResponse.success(Map.of("totalPreferences", total));
    }

    @PutMapping("/{userId}/channels/enable-all")
    public ApiResponse<Void> enableAllChannels(@PathVariable String userId) {
        try {
            notificationPreferenceService.enableAllChannels(userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PutMapping("/{userId}/channels/disable-all")
    public ApiResponse<Void> disableAllChannels(@PathVariable String userId) {
        try {
            notificationPreferenceService.disableAllChannels(userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
}
