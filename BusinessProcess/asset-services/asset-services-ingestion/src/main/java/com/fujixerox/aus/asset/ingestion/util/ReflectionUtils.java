package com.fujixerox.aus.asset.ingestion.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.documentum.fc.common.DfRuntimeException;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Object object, String fieldName) {
        try {
            Field field = getField(object.getClass(), fieldName);
            return (T) field.get(object);
        } catch (IllegalAccessException ex) {
            throw DfRuntimeException.convertToRuntimeException(ex);
        }
    }

    public static <T> void setValue(Object object, String fieldName, T value) {
        try {
            Field field = getField(object.getClass(), fieldName);
            field.set(object, value);
        } catch (IllegalAccessException ex) {
            throw DfRuntimeException.convertToRuntimeException(ex);
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> cls = clazz;
        while (true) {
            try {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ex) {
                if (cls == Object.class) {
                    DfRuntimeException.convertToRuntimeException(ex);
                }
                cls = cls.getSuperclass();
            }
        }
    }

    public static <T> T invokeMethod(Object object, String methodName,
            Object... parameters) {
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }
        Method method = getMethod(object.getClass(), methodName, parameterTypes);
        return invokeMethod(object, method, parameters);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object object, Method method,
            Object... parameters) {
        try {
            if (method.getReturnType().equals(Void.class)) {
                method.invoke(object, parameters);
                return null;
            } else {
                return (T) method.invoke(object, parameters);
            }
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw DfRuntimeException.convertToRuntimeException(ex);
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName,
            Class<?>... parameterTypes) {
        Class<?> cls = clazz;
        while (true) {
            try {
                Method method = cls.getDeclaredMethod(methodName,
                        parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ex) {
                if (cls == Object.class) {
                    throw DfRuntimeException.convertToRuntimeException(ex);
                }
                cls = cls.getSuperclass();
            }
        }
    }

    public static <T> T createNewObject(Class<?> clazz, Object... parameters) {
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }
        Constructor<T> constructor = getConstructor(clazz, parameterTypes);
        return createNewObject(constructor, parameters);
    }

    public static <T> T createNewObject(Constructor<T> constructor,
            Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InvocationTargetException | IllegalAccessException
                | InstantiationException ex) {
            throw DfRuntimeException.convertToRuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<?> clazz,
            Class<?>... parameterTypes) {
        try {
            Constructor<?> constructor = clazz
                    .getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return (Constructor<T>) constructor;
        } catch (NoSuchMethodException ex) {
            throw DfRuntimeException.convertToRuntimeException(ex);
        }
    }

}
