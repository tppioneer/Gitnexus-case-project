package com.example.telecom.change.plugin;

import com.example.telecom.change.aop.AuditOperationAspect;
import com.example.telecom.change.aop.InMemoryAuditSink;
import com.example.telecom.change.config.ChangeProperties;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import com.example.telecom.change.event.ChangeCompletedListener;
import com.example.telecom.change.event.ChangeNotificationListener;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case O: Framework, Event, AOP and Reflection Boundary Test.
 * Verifies ServiceLoader discovery, config-driven reflective plugin loading,
 * and documents the boundaries of static analysis.
 */
class CaseOFrameworkReflectionTest {

    @Test
    void serviceLoader_discoverBenchmarkPlugin() {
        List<ChangeValidationPlugin> plugins = new java.util.ArrayList<>();
        ServiceLoader<ChangeValidationPlugin> loader =
                ServiceLoader.load(ChangeValidationPlugin.class);
        for (ChangeValidationPlugin plugin : loader) {
            plugins.add(plugin);
        }
        assertTrue(plugins.stream().anyMatch(p -> p instanceof BenchmarkChangeValidationPlugin),
                "ServiceLoader must discover BenchmarkChangeValidationPlugin");
    }

    @Test
    void reflectivePluginLoad_executesBenchmarkPlugin() {
        ChangeProperties props = new ChangeProperties();
        ChangePluginLoader loader = new ChangePluginLoader(props);
        ChangeContext ctx = new ChangeContext("CHG-O-001", DeviceFamily.ROUTER, ChangeMode.LIVE);

        // Use the plugin class name from ChangeProperties (config-driven)
        ExecutionResult result = loader.executeConfiguredPlugin(ctx);
        assertTrue(result.isSuccess());
        assertEquals("BenchmarkChangeValidation", result.getExecutorName());
    }

    @Test
    void reflectivePluginLoad_invalidClass_returnsFailure() {
        ChangeProperties props = new ChangeProperties();
        ChangePluginLoader loader = new ChangePluginLoader(props);
        ChangeContext ctx = new ChangeContext("CHG-O-002", DeviceFamily.ROUTER, ChangeMode.LIVE);

        ExecutionResult result = loader.loadAndExecuteReflective(
                "com.example.telecom.change.plugin.NonExistentPlugin", ctx);
        assertFalse(result.isSuccess());
    }

    @Test
    void descriptorBasedExecution_usesClassNameFromConfig() {
        ChangeProperties props = new ChangeProperties();
        ChangePluginLoader loader = new ChangePluginLoader(props);
        PluginDescriptor descriptor = new PluginDescriptor(
                props.getPluginClassName(), "execute", "validate");
        ChangeContext ctx = new ChangeContext("CHG-O-003", DeviceFamily.ROUTER, ChangeMode.LIVE);

        ExecutionResult result = loader.executeFromDescriptor(descriptor, ctx);
        assertTrue(result.isSuccess());
    }

    @Test
    void reflectionBoundary_classForName_isNotStaticCallEdge() {
        // This test documents that Class.forName(...) is NOT a static call edge.
        String className = "com.example.telecom.change.plugin.BenchmarkChangeValidationPlugin";
        assertDoesNotThrow(() -> Class.forName(className));
    }

    @Test
    void pluginDescriptor_executeMethodName_isJustAString() {
        // PluginDescriptor.executeMethodName is configuration data, not a method call.
        PluginDescriptor desc = new PluginDescriptor("SomeClass", "execute", "validate");
        assertEquals("execute", desc.getExecuteMethodName());
    }

    @Test
    void auditOperationAspect_classExists() {
        // Verify the aspect class exists
        assertTrue(AuditOperationAspect.class.isAnnotationPresent(org.aspectj.lang.annotation.Aspect.class)
                || java.util.Arrays.stream(AuditOperationAspect.class.getMethods())
                .anyMatch(m -> m.getName().equals("around")));
    }

    @Test
    void eventListeners_classesExist() {
        // These are invoked by the Spring event dispatcher, not by direct call.
        assertNotNull(ChangeCompletedListener.class);
        assertNotNull(ChangeNotificationListener.class);
    }

    @Test
    void changeProperties_defaultPluginClassName_isBenchmarkPlugin() {
        ChangeProperties props = new ChangeProperties();
        assertEquals("com.example.telecom.change.plugin.BenchmarkChangeValidationPlugin",
                props.getPluginClassName());
    }
}
