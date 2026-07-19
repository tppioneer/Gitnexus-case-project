package com.example.telecom.change.annotation;

import com.example.telecom.change.domain.ChangeRisk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guards a class or method so that only changes with sufficient approval
 * may proceed. Applied at the type level on service classes and at the
 * method level on individual guarded operations.
 *
 * Also usable as a meta-annotation — composed into {@link CriticalChange}.
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeGuard {
    /** Minimum risk level for this guard. */
    ChangeRisk risk() default ChangeRisk.LOW;

    /** Whether approval is required before the guarded operation proceeds. */
    boolean requireApproval() default false;
}
