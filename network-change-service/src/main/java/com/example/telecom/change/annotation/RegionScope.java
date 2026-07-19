package com.example.telecom.change.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Scopes an operation, parameter or type use to a specific network region.
 * Can be applied to methods, parameters and type uses.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegionScope {
    /** The region code (e.g. "east", "west", "north"). */
    String value();

    /** Whether child regions are included in the scope. */
    boolean includeChildren() default false;
}
