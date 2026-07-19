package com.example.telecom.change.annotation;

import com.example.telecom.change.domain.AuditCategory;
import com.example.telecom.change.domain.ChangeRisk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composed (meta) annotation that combines {@link AuditOperation} and
 * {@link ChangeGuard} for critical changes. Applying {@code @CriticalChange}
 * to a method simultaneously declares it as a sensitive audit event and
 * a high-risk guarded operation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AuditOperation(action = "critical-change", category = AuditCategory.CHANGE, sensitive = true)
@ChangeGuard(risk = ChangeRisk.CRITICAL, requireApproval = true)
public @interface CriticalChange {
}
