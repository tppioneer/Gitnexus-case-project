package com.example.telecom.change.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for the repeatable {@link RequiredCapability}.
 * The Java compiler generates this automatically when multiple
 * {@code @RequiredCapability} annotations are applied to the same element.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredCapabilities {
    /** The array of required capabilities. */
    RequiredCapability[] value();
}
