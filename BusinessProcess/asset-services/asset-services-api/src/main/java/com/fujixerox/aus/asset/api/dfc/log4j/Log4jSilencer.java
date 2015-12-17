package com.fujixerox.aus.asset.api.dfc.log4j;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.documentum.fc.common.DfLogger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class Log4jSilencer {

    private Log4jSilencer() {
        super();
    }

    public static <T> T invoke(ILog4jSilenceInvoker<T> invoker,
            Class<?>... classes) {
        Map<Class<?>, Level> levels = new HashMap<>();
        try {
            for (Class<?> clazz : classes) {
                levels.put(clazz, switchLogLevel(clazz, Level.OFF));
            }
            return invoker.invoke(null);
        } finally {
            for (Map.Entry<Class<?>, Level> entry : levels.entrySet()) {
                switchLogLevel(entry.getKey(), entry.getValue());
            }
        }
    }

    private static Level switchLogLevel(Class<?> clazz, Level newLevel) {
        Logger logger = DfLogger.getLogger(clazz);
        Level old = logger.getLevel();
        logger.setLevel(newLevel);
        return old;
    }

}
