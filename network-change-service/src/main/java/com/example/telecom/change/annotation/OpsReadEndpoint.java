package com.example.telecom.change.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composed Spring mapping annotation for read-only ops endpoints.
 * Meta-annotated with {@code @RequestMapping(method = GET)} so methods
 * marked with {@code @OpsReadEndpoint} are mapped as HTTP GET automatically.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.GET)
public @interface OpsReadEndpoint {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};
}
