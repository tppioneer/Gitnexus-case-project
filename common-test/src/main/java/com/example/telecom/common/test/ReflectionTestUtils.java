package com.example.telecom.common.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility methods for accessing private fields and invoking private methods
 * via reflection.
 *
 * <p>All checked exceptions are wrapped in {@link RuntimeException} so callers
 * can use these helpers without explicit try-catch blocks.
 */
public final class ReflectionTestUtils {

    private ReflectionTestUtils() {
        // utility class
    }

    /**
     * Sets the value of a private field on the target object.
     *
     * @param target    the object whose field to set
     * @param fieldName the name of the field
     * @param value     the value to set
     * @throws IllegalArgumentException if the field cannot be found or made accessible
     */
    public static void setField(final Object target, final String fieldName, final Object value) {
        try {
            final Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "' on " + target.getClass(), e);
        }
    }

    /**
     * Gets the value of a private field on the target object.
     *
     * @param target    the object whose field to read
     * @param fieldName the name of the field
     * @param <T>       the expected field type
     * @return the current field value
     * @throws IllegalArgumentException if the field cannot be found or made accessible
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(final Object target, final String fieldName) {
        try {
            final Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to get field '" + fieldName + "' from " + target.getClass(), e);
        }
    }

    /**
     * Invokes a private method on the target object with the given arguments.
     *
     * @param target     the object whose method to invoke
     * @param methodName the name of the method
     * @param args       the method arguments
     * @param <T>        the expected return type
     * @return the return value of the method, or {@code null} for void methods
     * @throws IllegalArgumentException if the method cannot be found or invoked
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokePrivateMethod(final Object target, final String methodName, final Object... args) {
        try {
            final Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            final Method method = findMethod(target.getClass(), methodName, paramTypes);
            method.setAccessible(true);
            return (T) method.invoke(target, args);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Failed to invoke method '" + methodName + "' on " + target.getClass(), e);
        }
    }

    /**
     * Walks the class hierarchy to find a declared field.
     *
     * @param clazz     the class to search
     * @param fieldName the field name
     * @return the {@link Field}
     * @throws NoSuchFieldException if the field does not exist in the hierarchy
     */
    private static Field findField(Class<?> clazz, final String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in " + clazz);
    }

    /**
     * Walks the class hierarchy to find a declared method.
     *
     * @param clazz      the class to search
     * @param methodName the method name
     * @param paramTypes the parameter types
     * @return the {@link Method}
     * @throws NoSuchMethodException if the method does not exist in the hierarchy
     */
    private static Method findMethod(Class<?> clazz, final String methodName, final Class<?>[] paramTypes)
            throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, paramTypes);
            } catch (final NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method '" + methodName + "' not found in " + clazz);
    }
}
