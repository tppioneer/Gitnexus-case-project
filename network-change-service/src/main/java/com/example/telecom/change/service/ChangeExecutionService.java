package com.example.telecom.change.service;

import com.example.telecom.change.annotation.ChangeExecutorCandidate;
import com.example.telecom.change.annotation.ChangeGuard;
import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ExecutionResult;
import com.example.telecom.change.event.ChangeCompletedEvent;
import com.example.telecom.change.executor.ChangeExecutor;
import com.example.telecom.change.executor.ChangeExecutorRegistry;
import com.example.telecom.change.executor.ChangeStepExecutor;
import com.example.telecom.change.executor.RollbackHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.telecom.change.annotation.RegionScope;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/**
 * Core execution service — the primary DI injection scenario.
 *
 * Demonstrates all required DI patterns in a single constructor:
 * - @Qualifier selecting a specific bean
 * - @ChangeExecutorCandidate custom qualifier for collection injection
 * - Map&lt;String, T&gt; injection
 * - ObjectProvider&lt;T&gt; lazy resolution
 * - Class-level @Transactional
 */
@Service
@ChangeGuard(risk = ChangeRisk.MEDIUM)
@Transactional
public class ChangeExecutionService {

    private final ChangeExecutor safeExecutor;
    private final List<ChangeExecutor> executors;
    private final Map<String, ChangeExecutor> executorBeans;
    private final ObjectProvider<RollbackHandler> rollbackHandlerProvider;
    private final ChangeExecutorRegistry registry;
    private final ChangeStepExecutor stepExecutor;
    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService executorService;

    public ChangeExecutionService(
            @Qualifier("safeExecutor") ChangeExecutor safeExecutor,
            @ChangeExecutorCandidate List<ChangeExecutor> executors,
            Map<String, ChangeExecutor> executorBeans,
            ObjectProvider<RollbackHandler> rollbackHandlerProvider,
            ChangeExecutorRegistry registry,
            ChangeStepExecutor stepExecutor,
            ApplicationEventPublisher eventPublisher,
            ExecutorService executorService) {
        this.safeExecutor = safeExecutor;
        this.executors = executors;
        this.executorBeans = executorBeans;
        this.rollbackHandlerProvider = rollbackHandlerProvider;
        this.registry = registry;
        this.stepExecutor = stepExecutor;
        this.eventPublisher = eventPublisher;
        this.executorService = executorService;
    }

    /**
     * Dynamic dispatch — resolves the executor for the given device family
     * via the registry, then invokes execute on the resolved instance.
     */
    public ExecutionResult execute(ChangeContext context) {
        ChangeExecutor executor = registry.resolve(context.deviceFamily());
        ExecutionResult result = executor.execute(context);
        eventPublisher.publishEvent(new ChangeCompletedEvent(
                context.getChangeId(), result.isSuccess(), result.getMessage()));
        return result;
    }

    /**
     * Execute via the safe executor, which is injected by qualifier
     * and always resolves to SafeChangeExecutor regardless of device family.
     */
    public ExecutionResult executeSafe(ChangeContext context) {
        return safeExecutor.execute(context);
    }

    /** Return the class of the executor resolved for a given device family. */
    public Class<?> resolveExecutorClass(ChangeContext context) {
        ChangeExecutor executor = registry.resolve(context.deviceFamily());
        return executor.getClass();
    }

    /** Return the list of candidate executors injected via the custom qualifier. */
    public List<ChangeExecutor> getExecutors() { return executors; }

    /** Return the map of executor bean names to executor instances. */
    public Map<String, ChangeExecutor> getExecutorBeans() { return executorBeans; }

    /** Return the resolved rollback handler, or null if none is available. */
    public RollbackHandler getRollbackHandler() {
        return rollbackHandlerProvider.getIfAvailable();
    }

    /**
     * Callback / method reference scenario — submits plan.markRunning()
     * to an executor service via method reference.
     */
    public void submitAsyncMark(com.example.telecom.change.domain.NetworkChangePlan plan) {
        executorService.submit(plan::markRunning);
    }

    /**
     * Stream + method reference — maps step names to results via
     * stepExecutor::executeStep.
     */
    public List<ExecutionResult> executeSteps(List<String> stepNames) {
        return stepNames.stream()
                .map(stepExecutor::executeStep)
                .toList();
    }

    /**
     * Class-level @Transactional is inherited by this method.
     */
    public void complete(String planId, boolean success) {
        eventPublisher.publishEvent(new ChangeCompletedEvent(planId, success, "completed"));
    }

    /**
     * Demonstrates lambda with forEach — exercises callback-style invocation.
     */
    public void validateAll(List<ChangeContext> contexts) {
        executors.forEach(executor -> {
            for (ChangeContext ctx : contexts) {
                if (executor.supports() == ctx.deviceFamily() ||
                    executor.supports() == com.example.telecom.change.domain.DeviceFamily.ALL) {
                    executor.execute(ctx);
                }
            }
        });
    }

    /** Noise method with the same name "execute" as ChangeExecutor — but this is NOT an override. */
    public String execute(String planId) {
        return "executed-" + planId;
    }

    /**
     * Lambda forEach with validators — exercises interface callback invocation.
     * Returns true if all validators pass for the given context.
     */
    public boolean validateAllContexts(ChangeContext context,
                                       java.util.List<com.example.telecom.change.validation.ChangeValidator> validators) {
        java.util.concurrent.atomic.AtomicBoolean allPass = new java.util.concurrent.atomic.AtomicBoolean(true);
        validators.forEach(v -> {
            if (!v.validate(context)) {
                allPass.set(false);
            }
        });
        return allPass.get();
    }

    /**
     * Demonstrates TYPE_USE annotation on a generic type element.
     * The annotation applies to the String element type inside the List,
     * not to the method, the parameter, or the List type itself.
     */
    public void processRegionalCodes(List<@RegionScope("east") String> codes) {
        for (String code : codes) {
            if (code != null) {
                // process code
            }
        }
    }
}
