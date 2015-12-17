package com.fujixerox.aus.asset.web.resource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.util.ReflectionUtils;

import com.documentum.fc.common.DfLogger;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
@SuppressWarnings("PMD")
public class DfcCleanupListener implements ServletContextListener {

    public DfcCleanupListener() {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // do nothing
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        launchHooks();
        cleanupThreads();
        cleanupSecurityPolicy();
        cleanupLog4j();
    }

    private void cleanupLog4j() {
        Object loggers = getValue("s_loggers", DfLogger.class);
        if (loggers != null) {
            loggers = getValue("m_loggersMap", loggers);
        }
        Object prefixes = getValue("s_prefixes", DfLogger.class);
        Object counters = getValue("s_muteCounter", DfLogger.class);
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            Object threadLocals = getValue("threadLocals", thread);
            if (threadLocals == null) {
                continue;
            }
            if (loggers != null) {
                invoke("remove", threadLocals, loggers);
            }
            if (prefixes != null) {
                invoke("remove", threadLocals, prefixes);
            }
            if (counters != null) {
                invoke("remove", threadLocals, counters);
            }
        }
    }

    private void cleanupSecurityPolicy() {
        Policy.setPolicy(null);
    }

    @SuppressWarnings("unchecked")
    private void launchHooks() {
        try {
            Class clazz = Class.forName("java.lang.ApplicationShutdownHooks");
            Object hooks = getValue("hooks", clazz);
            if (hooks == null) {
                return;
            }
            List<Thread> threads = new ArrayList<Thread>();
            for (Thread thread : ((Map<Thread, Thread>) hooks).keySet()) {
                if (isInSameClassLoader(thread, getClass().getClassLoader())) {
                    threads.add(thread);
                }
            }
            for (Thread thread : threads) {
                Runtime.getRuntime().removeShutdownHook(thread);
            }
            for (Thread hook : threads) {
                hook.start();
            }
            for (Thread hook : threads) {
                try {
                    hook.join();
                } catch (InterruptedException ex) {
                    Logger.error(ex.getMessage(), ex);
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.error(ex.getMessage(), ex);
        }
    }

    private void cleanupThreads() {
        for (Thread thread : getThreadsWithSameClassLoader(getClass()
                .getClassLoader())) {
            Logger.error("Stopping thread {0}", null, thread.getName());
            stopThread(thread);
        }
    }

    private void stopThread(Thread thread) {
        if ("java.util.TimerThread".equals(thread.getClass().getName())) {
            cancelTimerThread(thread);
        }
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }

    private void cancelTimerThread(Thread thread) {
        Object queue = getValue("queue", thread);
        if (queue == null) {
            return;
        }
        synchronized (queue) {
            setField("newTasksMayBeScheduled", thread, false);
            invoke("clear", queue);
            invoke("notify", queue);
        }
    }

    private static <T> T invoke(String method, Object object, Object... args) {
        try {
            if (object == null) {
                return null;
            }
            Method mtd = ReflectionUtils.findMethod(object.getClass(), method);
            if (mtd == null) {
                return null;
            }
            return invoke(mtd, object, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Method method, Object object, Object... args)
        throws IllegalAccessException, InvocationTargetException {
        ReflectionUtils.makeAccessible(method);
        return (T) method.invoke(object, args);
    }

    private static void setField(String field, Object object, Object value) {
        try {
            if (object == null) {
                return;
            }
            Field fld = ReflectionUtils.findField(object.getClass(), field);
            if (fld == null) {
                return;
            }
            setField(fld, object, value);
        } catch (IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
        }
    }

    private static void setField(Field field, Object object, Object value)
        throws IllegalAccessException {
        ReflectionUtils.makeAccessible(field);
        field.set(object, value);
    }

    private static Object getValue(String field, Object object) {
        try {
            if (object == null) {
                return null;
            }
            Field fld = ReflectionUtils.findField(object.getClass(), field);
            if (fld == null) {
                return null;
            }
            return getValue(fld, object);
        } catch (IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
        }
        return null;
    }

    private static Object getValue(String field, Class clazz) {
        try {
            if (clazz == null) {
                return null;
            }
            Field fld = ReflectionUtils.findField(clazz, field);
            if (fld == null) {
                return null;
            }
            return getValue(fld, null);
        } catch (IllegalAccessException e) {
            Logger.error(e.getMessage(), e);
        }
        return null;
    }

    private static Object getValue(Field field, Object object)
        throws IllegalAccessException {
        ReflectionUtils.makeAccessible(field);
        return field.get(object);
    }

    private static List<Thread> getThreadsWithSameClassLoader(
            ClassLoader classLoader) {
        List<Thread> result = new ArrayList<Thread>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (isInSameClassLoader(thread, classLoader)) {
                result.add(thread);
            }
        }
        return result;
    }

    private static boolean isInSameClassLoader(Thread thread,
            ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }
        Object inheritedAccessControlContext = getValue(
                "inheritedAccessControlContext", thread);
        if (!(inheritedAccessControlContext instanceof AccessControlContext)) {
            return false;
        }
        Object contexts = getValue("context", inheritedAccessControlContext);
        if (!(contexts instanceof ProtectionDomain[])) {
            return false;
        }
        for (ProtectionDomain domain : (ProtectionDomain[]) contexts) {
            if (domain == null) {
                continue;
            }
            if (classLoader.equals(domain.getClassLoader())) {
                return true;
            }
        }
        return false;
    }

}
