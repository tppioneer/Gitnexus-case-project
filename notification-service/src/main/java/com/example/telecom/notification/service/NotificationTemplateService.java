package com.example.telecom.notification.service;

import com.example.telecom.common.workorder.WorkOrderEvent;
import com.example.telecom.notification.template.NotificationTemplate;
import com.example.telecom.notification.template.TemplateVariableRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationTemplateService {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    private TemplateVariableRegistry variableRegistry;
    private Map<String, NotificationTemplate> templateCache;

    public NotificationTemplateService() {
        this.variableRegistry = null;
        this.templateCache = new HashMap<>();
    }

    public NotificationTemplateService(TemplateVariableRegistry variableRegistry) {
        this.variableRegistry = variableRegistry;
        this.templateCache = new HashMap<>();
    }

    public String renderSubject(WorkOrderEvent event) {
        return "[Telecom Ops] Work Order " + event.getWorkOrderId()
                + " status: " + event.getFromStatus() + " → " + event.getToStatus();
    }

    public String renderBody(WorkOrderEvent event) {
        return "Work Order: " + event.getWorkOrderId() + "\n"
                + "Status transition: " + event.getFromStatus() + " → " + event.getToStatus() + "\n"
                + "Assignee: " + (event.getAssignee() != null ? event.getAssignee() : "unassigned") + "\n"
                + "Region: " + event.getMaintenanceRegionCode() + "\n"
                + "Time: " + event.getEventTimestamp();
    }

    public String renderSubject(String templateId, String contextId) {
        NotificationTemplate template = resolveTemplate(templateId);
        if (template == null) {
            return "Notification";
        }
        return resolveVariables(template.getSubjectTemplate(), contextId);
    }

    public String renderBody(String templateId, String contextId) {
        NotificationTemplate template = resolveTemplate(templateId);
        if (template == null) {
            return "No content available.";
        }
        return resolveVariables(template.getBodyTemplate(), contextId);
    }

    public String renderWithTemplate(NotificationTemplate template, String contextId) {
        if (template == null) {
            return "";
        }
        String subject = resolveVariables(template.getSubjectTemplate(), contextId);
        String body = resolveVariables(template.getBodyTemplate(), contextId);
        return subject + "\n\n" + body;
    }

    public String resolveVariables(String templateText, String contextId) {
        if (templateText == null || templateText.isEmpty()) {
            return templateText;
        }
        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(templateText);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String resolvedValue = resolveVariable(variableName, contextId);
            matcher.appendReplacement(result, Matcher.quoteReplacement(resolvedValue));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String resolveVariable(String variableName, String contextId) {
        if (variableRegistry == null) {
            return "{{" + variableName + "}}";
        }
        try {
            Map<String, String> variables = variableRegistry.resolveVariables(
                    determineResolverType(variableName), contextId);
            return variables.getOrDefault(variableName, "{{" + variableName + "}}");
        } catch (IllegalArgumentException e) {
            return "{{" + variableName + "}}";
        }
    }

    private String determineResolverType(String variableName) {
        if (variableName.contains("alarm") || variableName.contains("severity")) {
            return "Alarm";
        } else if (variableName.contains("sla") || variableName.contains("breach")) {
            return "Sla";
        }
        return "WorkOrder";
    }

    private NotificationTemplate resolveTemplate(String templateId) {
        return templateCache.get(templateId);
    }

    public void cacheTemplate(NotificationTemplate template) {
        if (template != null && template.getTemplateId() != null) {
            templateCache.put(template.getTemplateId(), template);
        }
    }

    public void setVariableRegistry(TemplateVariableRegistry variableRegistry) {
        this.variableRegistry = variableRegistry;
    }

    public Map<String, String> resolveAllVariables(String templateText, String contextId) {
        Map<String, String> result = new HashMap<>();
        if (templateText == null || templateText.isEmpty()) {
            return result;
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(templateText);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String resolvedValue = resolveVariable(variableName, contextId);
            result.put(variableName, resolvedValue);
        }
        return result;
    }

    public boolean hasUnresolvedVariables(String templateText) {
        if (templateText == null) {
            return false;
        }
        return VARIABLE_PATTERN.matcher(templateText).find();
    }
}
