package com.example.telecom.change.annotation;

import com.example.telecom.change.service.ChangeExecutionService;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies TYPE_USE annotation placement on generic type elements.
 *
 * The method {@code processRegionalCodes(List<@RegionScope("east") String>)}
 * annotates the String element inside the List, not the List type itself
 * and not the parameter. This test uses reflection to prove that the
 * annotation is on the correct target.
 */
class CaseITypeUseAnnotationTest {

    @Test
    void regionScope_targetsMethodParameterAndTypeUse() {
        // Verify @RegionScope has TYPE_USE in its @Target
        Target target = RegionScope.class.getAnnotation(Target.class);
        assertNotNull(target, "RegionScope must have @Target");

        boolean hasTypeUse = false;
        boolean hasParameter = false;
        boolean hasMethod = false;
        for (java.lang.annotation.ElementType et : target.value()) {
            if (et == java.lang.annotation.ElementType.TYPE_USE) hasTypeUse = true;
            if (et == java.lang.annotation.ElementType.PARAMETER) hasParameter = true;
            if (et == java.lang.annotation.ElementType.METHOD) hasMethod = true;
        }
        assertTrue(hasTypeUse, "RegionScope must target TYPE_USE");
        assertTrue(hasParameter, "RegionScope must target PARAMETER");
        assertTrue(hasMethod, "RegionScope must target METHOD");
    }

    @Test
    void processRegionalCodes_parameterIsAnnotatedParameterizedType() throws Exception {
        Method method = ChangeExecutionService.class.getMethod("processRegionalCodes", java.util.List.class);
        AnnotatedType[] annotatedParamTypes = method.getAnnotatedParameterTypes();
        assertEquals(1, annotatedParamTypes.length, "processRegionalCodes has one parameter");

        assertTrue(annotatedParamTypes[0] instanceof AnnotatedParameterizedType,
                "The List parameter must be an AnnotatedParameterizedType");
    }

    @Test
    void processRegionalCodes_typeUseAnnotationOnStringElement() throws Exception {
        Method method = ChangeExecutionService.class.getMethod("processRegionalCodes", java.util.List.class);
        AnnotatedType[] annotatedParamTypes = method.getAnnotatedParameterTypes();
        AnnotatedParameterizedType paramType = (AnnotatedParameterizedType) annotatedParamTypes[0];

        // The generic argument is String
        AnnotatedType[] typeArgs = paramType.getAnnotatedActualTypeArguments();
        assertEquals(1, typeArgs.length, "List<String> has one type argument");

        AnnotatedType stringType = typeArgs[0];

        // The @RegionScope annotation must be on the String type argument
        Annotation[] annotations = stringType.getAnnotations();
        boolean hasRegionScope = false;
        for (Annotation a : annotations) {
            if (a instanceof RegionScope) {
                hasRegionScope = true;
                RegionScope rs = (RegionScope) a;
                assertEquals("east", rs.value(), "RegionScope value must be 'east'");
            }
        }
        assertTrue(hasRegionScope,
                "@RegionScope must be present on the String type argument (TYPE_USE position)");
    }

    @Test
    void processRegionalCodes_noAnnotationOnParameterItself() throws Exception {
        Method method = ChangeExecutionService.class.getMethod("processRegionalCodes", java.util.List.class);

        // Parameter-level annotations (not TYPE_USE)
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        assertEquals(1, paramAnnotations.length);

        // The parameter itself should NOT have @RegionScope
        for (Annotation a : paramAnnotations[0]) {
            assertNotEquals(RegionScope.class, a.annotationType(),
                    "@RegionScope must NOT be on the parameter itself — it must be on the type argument");
        }
    }
}
