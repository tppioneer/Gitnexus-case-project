package com.example.telecom.notification.template;

import java.util.Map;

public interface TemplateVariableResolver {
    Map<String, String> resolve(String contextId);
}
