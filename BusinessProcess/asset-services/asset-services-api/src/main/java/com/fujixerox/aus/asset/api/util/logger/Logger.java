package com.fujixerox.aus.asset.api.util.logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class Logger {

    private static final String LOG2STREAM_PROPERTY = Logger.class.getName()
            + ".log2sysout";

    private static final Boolean LOG_2_STREAM_VALUE;

    private static final String CLASS = Logger.class.getName();

    private static final ThreadLocal<String> SOURCE = new ThreadLocal<String>();

    static {
        String param = System.getenv(LOG2STREAM_PROPERTY);
        if (param == null) {
            param = System.getProperty(LOG2STREAM_PROPERTY);
        }
        LOG_2_STREAM_VALUE = param != null
                && "true".equalsIgnoreCase(param.trim());
    }

    private Logger() {
        super();
    }

    public static boolean isEnabled(LogLevel level, Object source) {
        return level.isEnabled(source, LOG_2_STREAM_VALUE);
    }

    public static boolean isDebugEnabled() {
        return LogLevel.DEBUG.isEnabled(getSource(), LOG_2_STREAM_VALUE);
    }

    public static boolean isDebugEnabled(Object source) {
        return LogLevel.DEBUG.isEnabled(source, LOG_2_STREAM_VALUE);
    }

    private static void log(LogLevel level, Object source, String message) {
        level.log(source, message, null, null, LOG_2_STREAM_VALUE);
    }

    public static void log(LogLevel level, Object source, String message,
            Throwable throwable) {
        level.log(source, message, null, throwable, LOG_2_STREAM_VALUE);
    }

    private static void log(LogLevel level, Object source, String message,
            Object... params) {
        level.log(source, message, params, null, LOG_2_STREAM_VALUE);
    }

    private static void log(LogLevel level, Object source, String message,
            Throwable throwable, Object... params) {
        level.log(source, message, params, throwable, LOG_2_STREAM_VALUE);
    }

    public static void error(String message, Throwable throwable,
            Object... params) {
        log(LogLevel.ERROR, getSource(), message, throwable, params);
    }

    public static void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, getSource(), message, throwable);
    }

    public static void error(String message, Object... params) {
        log(LogLevel.ERROR, getSource(), message, params);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, getSource(), message);
    }

    public static void error(Throwable throwable) {
        log(LogLevel.ERROR, getSource(), null, throwable);
    }

    public static void warn(String message, Throwable throwable,
            Object... params) {
        log(LogLevel.WARN, getSource(), message, throwable, params);
    }

    public static void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, getSource(), message, throwable);
    }

    public static void warn(String message, Object... params) {
        log(LogLevel.WARN, getSource(), message, params);
    }

    public static void warn(String message) {
        log(LogLevel.WARN, getSource(), message);
    }

    public static void warn(Throwable throwable) {
        log(LogLevel.WARN, getSource(), null, throwable);
    }

    public static void info(String message, Throwable throwable,
            Object... params) {
        log(LogLevel.INFO, getSource(), message, throwable, params);
    }

    public static void info(String message, Throwable throwable) {
        log(LogLevel.INFO, getSource(), message, throwable);
    }

    public static void info(String message, Object... params) {
        log(LogLevel.INFO, getSource(), message, params);
    }

    public static void info(String message) {
        log(LogLevel.INFO, getSource(), message);
    }

    public static void info(Throwable throwable) {
        log(LogLevel.INFO, getSource(), null, throwable);
    }

    public static void debug(String message, Throwable throwable,
            Object... params) {
        log(LogLevel.DEBUG, getSource(), message, throwable, params);
    }

    public static void debug(String message, Throwable throwable) {
        log(LogLevel.DEBUG, getSource(), message, throwable);
    }

    public static void debug(String message, Object... params) {
        log(LogLevel.DEBUG, getSource(), message, params);
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, getSource(), message);
    }

    public static void debug(Throwable throwable) {
        log(LogLevel.DEBUG, getSource(), null, throwable);
    }

    public static void trace(String message, Throwable throwable,
            Object... params) {
        log(LogLevel.TRACE, getSource(), message, throwable, params);
    }

    public static void trace(String message, Throwable throwable) {
        log(LogLevel.TRACE, getSource(), message, throwable);
    }

    public static void trace(String message, Object... params) {
        log(LogLevel.TRACE, getSource(), message, params);
    }

    public static void trace(String message) {
        log(LogLevel.TRACE, getSource(), message);
    }

    public static void trace(Throwable throwable) {
        log(LogLevel.TRACE, getSource(), null, throwable);
    }

    public static void setCaller(String caller) {
        SOURCE.set(caller);
    }

    public static void removeCaller() {
        SOURCE.remove();
    }

    private static String getSource() {
        String caller = SOURCE.get();
        if (caller != null) {
            return caller;
        }
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1, n = elements.length; i < n; i++) {
            if (CLASS.equals(elements[i].getClassName())) {
                continue;
            }
            return String.format("%s.%s", elements[i].getClassName(),
                    elements[i].getMethodName());
        }
        return CLASS;
    }

}
