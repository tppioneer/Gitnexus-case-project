package com.example.telecom.notification.template;

import java.util.Map;

public class AlarmTemplateVariableResolver implements TemplateVariableResolver {

    @Override
    public Map<String, String> resolve(String alarmId) {
        return Map.of(
                "alarmId", alarmId,
                "severity", "UNKNOWN",
                "deviceId", "UNKNOWN",
                "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}
