package com.example.telecom.change.plugin;

import com.example.telecom.change.config.ChangeProperties;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Loads {@code ChangeValidationPlugin} implementations via both
 * {@code ServiceLoader} and reflective instantiation. The default
 * plugin class name is read from {@code ChangeProperties}.
 *
 * {@code Class.forName}, {@code Method.invoke}, and
 * {@code ServiceLoader.load} represent edges that static call-graph
 * analysis cannot resolve.
 */
@Component
public class ChangePluginLoader {

    private static final Logger log = LoggerFactory.getLogger(ChangePluginLoader.class);

    private final ChangeProperties properties;

    public ChangePluginLoader(ChangeProperties properties) {
        this.properties = properties;
    }

    /** Load plugins via ServiceLoader — discovers implementations from META-INF/services. */
    public List<ChangeValidationPlugin> loadViaServiceLoader() {
        List<ChangeValidationPlugin> plugins = new ArrayList<>();
        ServiceLoader<ChangeValidationPlugin> loader =
                ServiceLoader.load(ChangeValidationPlugin.class);
        for (ChangeValidationPlugin plugin : loader) {
            plugins.add(plugin);
            log.info("Discovered plugin via ServiceLoader: {}", plugin.name());
        }
        return plugins;
    }

    /** Load a plugin reflectively by class name — exercises Class.forName + Method.invoke. */
    public ExecutionResult loadAndExecuteReflective(String pluginClassName, ChangeContext context) {
        try {
            Class<?> type = Class.forName(pluginClassName);
            Object plugin = type.getDeclaredConstructor().newInstance();
            Method method = type.getMethod("execute", ChangeContext.class);
            return (ExecutionResult) method.invoke(plugin, context);
        } catch (ReflectiveOperationException e) {
            log.error("Failed to load plugin: {}", pluginClassName, e);
            return ExecutionResult.failure("ChangePluginLoader",
                    "Plugin load failed: " + e.getMessage());
        }
    }

    /** Load a plugin via descriptor — class name comes from configuration data. */
    public ExecutionResult executeFromDescriptor(PluginDescriptor descriptor, ChangeContext context) {
        return loadAndExecuteReflective(descriptor.getClassName(), context);
    }

    /**
     * Execute the default plugin configured via {@code telecom.change.plugin-class-name}.
     * The class name comes from properties — it is NOT a static code reference.
     */
    public ExecutionResult executeConfiguredPlugin(ChangeContext context) {
        String className = properties.getPluginClassName();
        return loadAndExecuteReflective(className, context);
    }
}
