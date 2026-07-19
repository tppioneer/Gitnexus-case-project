package com.example.telecom.change.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a required capability for the annotated type or method.
 * Repeatable — use multiple instances to declare compound requirements.
 *
 * Example:
 * <pre>
 * {@literal @}RequiredCapability("network.write")
 * {@literal @}RequiredCapability("audit.log")
 * public class ChangeExecutionService { ... }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequiredCapabilities.class)
public @interface RequiredCapability {
    /** The capability identifier. */
    String value();
}
