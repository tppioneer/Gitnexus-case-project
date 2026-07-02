package com.example.telecom.notification.template;

import java.util.*;

public class TemplateVariableRegistry {
    private final Map<String, TemplateVariableResolver> resolverMap;

    public TemplateVariableRegistry(List<TemplateVariableResolver> resolvers) {
        this.resolverMap = new HashMap<>();
        for (TemplateVariableResolver resolver : resolvers) {
            String key = resolver.getClass().getSimpleName().replace("TemplateVariableResolver", "");
            resolverMap.put(key, resolver);
        }
    }

    public TemplateVariableResolver resolve(String type) {
        TemplateVariableResolver resolver = resolverMap.get(type);
        if (resolver == null) {
            throw new IllegalArgumentException("No template variable resolver for: " + type);
        }
        return resolver;
    }

    public Map<String, String> resolveVariables(String type, String contextId) {
        return resolve(type).resolve(contextId);
    }
}
