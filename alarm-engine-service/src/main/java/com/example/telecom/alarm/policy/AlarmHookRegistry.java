package com.example.telecom.alarm.policy;

import com.example.telecom.common.alarm.AlarmRecord;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry that manages alarm lifecycle hooks. Hooks are registered,
 * unregistered, and executed in order when alarms go through lifecycle
 * transitions.
 */
public class AlarmHookRegistry {

    private final List<AlarmLifecycleHook> hooks = new CopyOnWriteArrayList<>();

    public AlarmHookRegistry() {
    }

    /**
     * Registers a lifecycle hook.
     *
     * @param hook the hook to register
     */
    public void register(AlarmLifecycleHook hook) {
        Objects.requireNonNull(hook, "hook must not be null");
        if (!hooks.contains(hook)) {
            hooks.add(hook);
        }
    }

    /**
     * Unregisters a lifecycle hook.
     *
     * @param hook the hook to unregister
     * @return true if the hook was found and removed
     */
    public boolean unregister(AlarmLifecycleHook hook) {
        Objects.requireNonNull(hook, "hook must not be null");
        return hooks.remove(hook);
    }

    /**
     * Returns an unmodifiable view of all registered hooks.
     */
    public List<AlarmLifecycleHook> getHooks() {
        return Collections.unmodifiableList(hooks);
    }

    /**
     * Executes all registered hooks on the given alarm, in registration order.
     * Each hook is executed regardless of whether previous hooks threw exceptions.
     *
     * @param alarm the alarm to process through all hooks
     */
    public void executeHooks(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        for (AlarmLifecycleHook hook : hooks) {
            try {
                hook.process(alarm);
            } catch (Exception e) {
                System.err.println("[HookRegistry] Error executing hook " + hook.getClass().getSimpleName()
                        + ": " + e.getMessage());
            }
        }
    }
}
