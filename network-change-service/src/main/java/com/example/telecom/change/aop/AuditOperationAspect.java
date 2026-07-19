package com.example.telecom.change.aop;

import com.example.telecom.change.annotation.AuditOperation;
import com.example.telecom.change.annotation.CriticalChange;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP aspect that intercepts methods annotated with {@code @AuditOperation}
 * OR with composed annotations that carry {@code @AuditOperation} as a
 * meta-annotation (e.g. {@code @CriticalChange}).
 *
 * The {@code @annotation(auditOperation)} pointcut matches only direct
 * applications, so the composed-annotation path uses a separate advice
 * that calls {@code AnnotatedElementUtils.getMergedAnnotation} to
 * resolve the effective {@code @AuditOperation} through the meta chain.
 */
@Aspect
@Component
public class AuditOperationAspect {

    private final AuditSink auditSink;

    public AuditOperationAspect(AuditSink auditSink) {
        this.auditSink = auditSink;
    }

    /** Direct @AuditOperation match. */
    @Around("@annotation(auditOperation)")
    public Object around(ProceedingJoinPoint joinPoint,
                         AuditOperation auditOperation) throws Throwable {
        return invokeWithAudit(joinPoint, auditOperation);
    }

    /**
     * Composed annotation match — captures @CriticalChange and any other
     * method annotated with a meta-annotation carrying @AuditOperation.
     * This advice does NOT fire for methods already matched by the direct
     * pointcut above (Spring's AOP framework handles this to prevent
     * double-advice on the same method).
     */
    @Around("@annotation(com.example.telecom.change.annotation.CriticalChange)")
    public Object aroundCritical(ProceedingJoinPoint joinPoint) throws Throwable {
        AuditOperation merged = resolveAuditOperation(joinPoint);
        if (merged == null) {
            return joinPoint.proceed();
        }
        return invokeWithAudit(joinPoint, merged);
    }

    private Object invokeWithAudit(ProceedingJoinPoint joinPoint,
                                   AuditOperation auditOperation) throws Throwable {
        String action = auditOperation.action();
        String signature = joinPoint.getSignature().toShortString();
        auditSink.before(action, signature);
        try {
            Object result = joinPoint.proceed();
            auditSink.after(action, true);
            return result;
        } catch (Throwable error) {
            auditSink.after(action, false);
            throw error;
        }
    }

    private AuditOperation resolveAuditOperation(ProceedingJoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?> targetClass = joinPoint.getTarget().getClass();
            for (Method m : targetClass.getMethods()) {
                if (m.getName().equals(methodName)
                        && m.getParameterCount() == joinPoint.getArgs().length) {
                    AuditOperation merged = AnnotatedElementUtils.getMergedAnnotation(m, AuditOperation.class);
                    if (merged != null) {
                        return merged;
                    }
                }
            }
        } catch (Exception ignored) {
            // Fall through — proceed without audit
        }
        return null;
    }
}
