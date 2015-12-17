package com.fujixerox.aus.asset.api.util.cache.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class ThreadLocalStorage {

    private static final ThreadLocalStorage INSTANCE = new ThreadLocalStorage();

    private final ThreadLocal<Map<Object, Object>> _cache = new ThreadLocal<Map<Object, Object>>();

    @SuppressWarnings("PMD.AvoidUsingVolatile")
    private volatile boolean _active;

    private ThreadLocalStorage() {
        super();
    }

    public static boolean isActive() {
        return getInstance()._active;
    }

    public static void setActive(boolean active) {
        getInstance()._active = active;
    }

    private static ThreadLocalStorage getInstance() {
        return INSTANCE;
    }

    public static void clear() {
        Map cache = getCache(false);
        if (cache == null) {
            return;
        }
        cache.clear();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object key) {
        Map cache = getCache(false);
        if (cache == null) {
            return null;
        }
        return (T) cache.get(key);
    }

    public static void put(Object key, Object object) {
        Map<Object, Object> cache = getCache();
        cache.put(key, object);
    }

    public static void remove(Object key) {
        Map<Object, Object> cache = getCache();
        cache.remove(key);
    }

    public static boolean containsKey(String key) {
        Map<Object, Object> cache = getCache();
        return cache.containsKey(key);
    }

    private static Map<Object, Object> getCache() {
        return getCache(true);
    }

    private static Map<Object, Object> getCache(boolean create) {
        Map<Object, Object> cache = getInstance()._cache.get();
        if (create && cache == null) {
            cache = new HashMap<Object, Object>();
            getInstance()._cache.set(cache);
        }
        return cache;
    }

}
