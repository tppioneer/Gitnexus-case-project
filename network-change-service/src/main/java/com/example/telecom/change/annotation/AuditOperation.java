package com.example.telecom.change.annotation;

import com.example.telecom.change.domain.AuditCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be audited by the AOP layer. When applied, the
 * {@code AuditOperationAspect} records before/after events around the
 * method invocation.
 *
 * This annotation is also usable as a meta-annotation — it is referenced
 * by {@link CriticalChange} to propagate audit semantics.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditOperation {
    /** Human-readable action name for the audit log. */
    String action();

    /** Category of the audited operation. */
    AuditCategory category();

    /** Whether the operation involves sensitive data. */
    boolean sensitive() default false;
}
