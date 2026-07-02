package com.example.telecom.notification.channel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationChannelRegistry {

    private final Map<String, NotificationChannel> channelMap;

    public NotificationChannelRegistry(List<NotificationChannel> channels) {
        this.channelMap = channels.stream()
                .collect(Collectors.toMap(
                        c -> c.getClass().getSimpleName().replace("NotificationChannel", "").toUpperCase(),
                        c -> c
                ));
    }

    public NotificationChannel resolve(String channelType) {
        NotificationChannel channel = channelMap.get(channelType.toUpperCase());
        if (channel == null) {
            throw new IllegalArgumentException("Unknown notification channel: " + channelType);
        }
        return channel;
    }
}
