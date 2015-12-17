package com.fujixerox.aus.asset.api.util;

import java.io.InputStream;
import java.util.Collection;

import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class CoreUtils {

    private CoreUtils() {
        super();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean inList(String str, String... values) {
        if (str == null) {
            return false;
        }
        if (values == null) {
            return false;
        }
        for (String val : values) {
            if (str.equals(val)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("PMD")
    public static InputStream getResourceAsStream(Class<?> clazz, String name) {
        InputStream stream = clazz.getResourceAsStream(name);
        if (stream != null) {
            Logger.debug("Resource {0} found as resource of class {1}", name,
                    clazz.getName());
            return stream;
        }

        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(name);
        }
        if (stream != null) {
            Logger.debug("Resource {0} found in classloader", name);
            return stream;
        }

        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(name);
        }
        if (stream != null) {
            Logger.debug("Resource {0} found in context classloader", name);
            return stream;
        }

        stream = ClassLoader.getSystemResourceAsStream(name);
        if (stream != null) {
            Logger.debug("Resource {0} found in system classloader", name);
            return stream;
        }
        return null;
    }

}
