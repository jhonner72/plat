package com.fujixerox.aus.asset.api.util.cache.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class ExecutionKey {

    private final transient Method _method;

    private final transient Object _target;

    private final transient Object[] _arguments;

    public ExecutionKey(JoinPoint point) {
        _method = MethodSignature.class.cast(point.getSignature()).getMethod();
        _target = getTarget(point);
        _arguments = point.getArgs();
    }

    @Override
    public int hashCode() {
        return _method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExecutionKey)) {
            return false;
        }
        ExecutionKey executionKey = ExecutionKey.class.cast(obj);
        if (!executionKey._method.equals(_method)) {
            return false;
        }
        if (!_target.equals(executionKey._target)) {
            return false;
        }
        return Arrays.deepEquals(executionKey._arguments, _arguments);
    }

    private static Object getTarget(final JoinPoint point) {
        Method method = MethodSignature.class.cast(point.getSignature())
                .getMethod();
        if (Modifier.isStatic(method.getModifiers())) {
            return method.getDeclaringClass();
        }
        return point.getTarget();
    }

}
