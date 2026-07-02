package com.example.telecom.notification.template;

import java.util.Map;

public class SlaTemplateVariableResolver implements TemplateVariableResolver {

    @Override
    public Map<String, String> resolve(String workOrderId) {
        return Map.of(
                "workOrderId", workOrderId,
                "slaStatus", "BREACHED",
                "elapsedMinutes", "0",
                "thresholdMinutes", "0"
        );
    }
}
