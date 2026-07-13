package com.example.telecom.notification.service;

import com.example.telecom.common.user.NotificationPreference;
import com.example.telecom.notification.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public NotificationPreferenceService(NotificationPreferenceRepository notificationPreferenceRepository) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    public NotificationPreference getPreference(String userId) {
        Optional<NotificationPreference> optional = notificationPreferenceRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Notification preference not found for userId: " + userId);
        }
        return optional.get();
    }

    public NotificationPreference createOrUpdatePreference(NotificationPreference preference) {
        if (preference == null) {
            throw new IllegalArgumentException("Preference must not be null");
        }
        if (preference.getUserId() == null || preference.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID must not be empty");
        }
        return notificationPreferenceRepository.save(preference);
    }

    public void updateChannelEnabled(String userId, Map<String, Boolean> channelMap) {
        if (channelMap == null || channelMap.isEmpty()) {
            throw new IllegalArgumentException("Channel map must not be null or empty");
        }
        NotificationPreference preference = getPreference(userId);
        for (Map.Entry<String, Boolean> entry : channelMap.entrySet()) {
            if (entry.getKey() != null) {
                preference.getChannelEnabled().put(entry.getKey(), entry.getValue());
            }
        }
        notificationPreferenceRepository.save(preference);
    }

    public void updateQuietHours(String userId, int startHour, int endHour) {
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23) {
            throw new IllegalArgumentException("Hours must be between 0 and 23");
        }
        NotificationPreference preference = getPreference(userId);
        preference.setQuietHoursEnabled(true);
        preference.setQuietStartHour(startHour);
        preference.setQuietEndHour(endHour);
        notificationPreferenceRepository.save(preference);
    }

    public void disableQuietHours(String userId) {
        NotificationPreference preference = getPreference(userId);
        preference.setQuietHoursEnabled(false);
        notificationPreferenceRepository.save(preference);
    }

    public void deletePreference(String userId) {
        NotificationPreference preference = getPreference(userId);
        notificationPreferenceRepository.delete(userId);
    }

    public List<NotificationPreference> getAllPreferences() {
        return notificationPreferenceRepository.findAll();
    }

    public boolean isChannelAllowed(String userId, String channel) {
        NotificationPreference preference = getPreference(userId);
        boolean channelEnabled = preference.isChannelEnabled(channel);
        if (!channelEnabled) {
            return false;
        }
        return !isInQuietHours(preference);
    }

    public List<NotificationPreference> findByChannelEnabled(String channel) {
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel must not be empty");
        }
        return notificationPreferenceRepository.findByChannelEnabled(channel);
    }

    public long getTotalPreferences() {
        return notificationPreferenceRepository.count();
    }

    public void enableAllChannels(String userId) {
        NotificationPreference preference = getPreference(userId);
        for (String channel : preference.getPreferredChannels()) {
            preference.getChannelEnabled().put(channel, true);
        }
        notificationPreferenceRepository.save(preference);
    }

    public void disableAllChannels(String userId) {
        NotificationPreference preference = getPreference(userId);
        for (String channel : preference.getPreferredChannels()) {
            preference.getChannelEnabled().put(channel, false);
        }
        notificationPreferenceRepository.save(preference);
    }

    private boolean isInQuietHours(NotificationPreference preference) {
        int currentHour = LocalTime.now().getHour();
        return preference.isInQuietHours(currentHour);
    }
}
