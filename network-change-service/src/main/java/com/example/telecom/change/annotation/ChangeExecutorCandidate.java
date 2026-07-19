package com.example.telecom.change.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Spring qualifier that marks a {@code ChangeExecutor} implementation
 * as a registry candidate. Executors without this annotation are excluded
 * from the dynamic registry even if they implement the same interface.
 *
 * This is a meta-annotation built on Spring's {@code @Qualifier}.
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface ChangeExecutorCandidate {
}
