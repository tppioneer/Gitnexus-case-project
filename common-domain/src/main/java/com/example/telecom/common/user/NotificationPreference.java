package com.example.telecom.common.user;

import java.util.*;

public class NotificationPreference {
    private String userId;
    private List<String> preferredChannels; // SMS, EMAIL, WECOM
    private Map<String, Boolean> channelEnabled;
    private boolean quietHoursEnabled;
    private int quietStartHour;
    private int quietEndHour;

    public NotificationPreference() {
        this.preferredChannels = new ArrayList<>();
        this.channelEnabled = new HashMap<>();
    }

    public NotificationPreference(String userId, List<String> preferredChannels) {
        this.userId = userId;
        this.preferredChannels = new ArrayList<>(preferredChannels);
        this.channelEnabled = new HashMap<>();
        for (String ch : preferredChannels) {
            this.channelEnabled.put(ch, true);
        }
        this.quietHoursEnabled = false;
        this.quietStartHour = 22;
        this.quietEndHour = 7;
    }

    public boolean isChannelEnabled(String channel) {
        return channelEnabled.getOrDefault(channel, false);
    }

    public boolean isInQuietHours(int hour) {
        if (!quietHoursEnabled) return false;
        if (quietStartHour > quietEndHour) {
            return hour >= quietStartHour || hour < quietEndHour;
        }
        return hour >= quietStartHour && hour < quietEndHour;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<String> getPreferredChannels() { return preferredChannels; }
    public void setPreferredChannels(List<String> preferredChannels) { this.preferredChannels = preferredChannels; }
    public Map<String, Boolean> getChannelEnabled() { return channelEnabled; }
    public void setChannelEnabled(Map<String, Boolean> channelEnabled) { this.channelEnabled = channelEnabled; }
    public boolean isQuietHoursEnabled() { return quietHoursEnabled; }
    public void setQuietHoursEnabled(boolean quietHoursEnabled) { this.quietHoursEnabled = quietHoursEnabled; }
    public int getQuietStartHour() { return quietStartHour; }
    public void setQuietStartHour(int quietStartHour) { this.quietStartHour = quietStartHour; }
    public int getQuietEndHour() { return quietEndHour; }
    public void setQuietEndHour(int quietEndHour) { this.quietEndHour = quietEndHour; }
}
