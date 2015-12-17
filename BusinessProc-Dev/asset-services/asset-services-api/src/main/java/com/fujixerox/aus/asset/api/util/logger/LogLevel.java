package com.fujixerox.aus.asset.api.util.logger;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.slf4j.LoggerFactory;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public enum LogLevel {

    TRACE {

        @Override
        boolean isEnabled(Object source, boolean log2Stream) {
            return getLogger(source).isTraceEnabled() || log2Stream;
        }

        @Override
        protected void doLog(Object source, String message, Object[] params,
                Throwable throwable, boolean log2Stream) {
            getLogger(source).trace(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    DEBUG {

        @Override
        boolean isEnabled(Object source, boolean log2Stream) {
            return getLogger(source).isDebugEnabled() || log2Stream;
        }

        @Override
        protected void doLog(Object source, String message, Object[] params,
                Throwable throwable, boolean log2Stream) {
            getLogger(source).debug(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    INFO {

        @Override
        boolean isEnabled(Object source, boolean log2Stream) {
            return getLogger(source).isInfoEnabled() || log2Stream;
        }

        @Override
        protected void doLog(Object source, String message, Object[] params,
                Throwable throwable, boolean log2Stream) {
            getLogger(source).info(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    WARN {

        @Override
        boolean isEnabled(Object source, boolean log2Stream) {
            return getLogger(source).isWarnEnabled() || log2Stream;
        }

        @Override
        protected void doLog(Object source, String message, Object[] params,
                Throwable throwable, boolean log2Stream) {
            getLogger(source).warn(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    ERROR {

        @Override
        boolean isEnabled(Object source, boolean log2Stream) {
            return getLogger(source).isErrorEnabled() || log2Stream;
        }

        @Override
        protected void doLog(Object source, String message, Object[] params,
                Throwable throwable, boolean log2Stream) {
            getLogger(source).error(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Syserr(source, this, message, params, throwable);
            }
        }

    };

    abstract boolean isEnabled(Object source, boolean log2Stream);

    protected abstract void doLog(Object source, String message,
            Object[] params, Throwable throwable, boolean log2Stream);

    void log(Object source, String message, Object[] params,
            Throwable throwable, boolean log2Stream) {
        try {
            if (isEnabled(source, log2Stream)) {
                doLog(source, nullToBlank(message), params, throwable,
                        log2Stream);
            }
        } catch (Exception | Error ex) {
            log2Syserr(this, ERROR, ex.getMessage(), null, ex);
        }
    }

    private static void log2Sysout(Object source, LogLevel level,
            String message, Object[] params, Throwable throwable) {
        log2Stream(source, level, message, params, throwable, System.out);
    }

    private static void log2Syserr(Object source, LogLevel level,
            String message, Object[] params, Throwable throwable) {
        log2Stream(source, level, message, params, throwable, System.err);
    }

    private static void log2Stream(Object source, LogLevel level,
            String message, Object[] params, Throwable throwable,
            PrintStream stream) {
        String format = "%-60s: ";
        if (source == null) {
            stream.print(String.format(format, "NULL"));
        } else if (source instanceof String) {
            stream.print(String.format(format, (String) source));
        } else if (source instanceof Class) {
            stream.print(String.format(format, ((Class<?>) source).getName()));
        } else {
            stream.print(String.format(format, source.getClass().getName()));
        }
        stream.print(String.format("%5s: ", level));
        stream.println(getMessage(message, params));
        if (throwable != null) {
            throwable.printStackTrace(stream);
        }
    }

    private static String nullToBlank(String str) {
        if (str != null) {
            return str;
        }
        return "";
    }

    private static org.slf4j.Logger getLogger(Object source) {
        if (source instanceof String) {
            return LoggerFactory.getLogger((String) source);
        }
        if (source instanceof Class) {
            return LoggerFactory.getLogger((Class) source);
        }
        return LoggerFactory.getLogger(source.getClass());
    }

    private static String getMessage(String message, Object[] params) {
        if (message != null && params != null) {
            return MessageFormat.format(message, params);
        }
        return message;
    }

}
