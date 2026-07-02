package com.example.telecom.notification.template;

import java.util.Map;

public class WorkOrderTemplateVariableResolver implements TemplateVariableResolver {

    @Override
    public Map<String, String> resolve(String workOrderId) {
        return Map.of(
                "workOrderId", workOrderId,
                "status", "UNKNOWN",
                "assignee", "UNASSIGNED",
                "regionCode", "UNKNOWN"
        );
    }
}
