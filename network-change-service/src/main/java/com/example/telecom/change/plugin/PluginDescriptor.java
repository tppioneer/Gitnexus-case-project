package com.example.telecom.change.plugin;

/**
 * Descriptor for a plugin loaded via reflection. Stores the class name
 * and method names as configuration data — the actual class is resolved
 * at runtime via {@code Class.forName}.
 */
public class PluginDescriptor {
    private final String className;
    private final String executeMethodName;
    private final String validateMethodName;

    public PluginDescriptor(String className, String executeMethodName, String validateMethodName) {
        this.className = className;
        this.executeMethodName = executeMethodName;
        this.validateMethodName = validateMethodName;
    }

    public String getClassName() { return className; }
    /** The method name to invoke via reflection — matches "execute" by convention. */
    public String getExecuteMethodName() { return executeMethodName; }
    public String getValidateMethodName() { return validateMethodName; }
}
