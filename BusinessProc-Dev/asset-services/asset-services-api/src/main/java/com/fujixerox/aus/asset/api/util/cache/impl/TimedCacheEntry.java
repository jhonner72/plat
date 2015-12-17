package com.fujixerox.aus.asset.api.util.cache.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
class TimedCacheEntry<K, V> extends CacheEntry<K, V> {

    private final AtomicLong _expirationTime;

    private final AtomicInteger _referenceCount = new AtomicInteger(0);

    public TimedCacheEntry(TimedCacheEntry<K, V> value) {
        this(value.getKey(), value.getValue(), value._expirationTime.get());
    }

    TimedCacheEntry(K key, V value, long expirationTime) {
        super(key, value);
        _expirationTime = new AtomicLong(expirationTime);
    }

    public boolean isExpired() {
        return isExpired(System.currentTimeMillis());
    }

    public boolean isExpired(long timestamp) {
        if (hasReferences()) {
            return false;
        }
        return getExpirationTime() < timestamp;
    }

    boolean hasReferences() {
        return _referenceCount.get() != 0;
    }

    long incrementReferenceCount() {
        return _referenceCount.incrementAndGet();
    }

    long decrementReferenceCount() {
        return _referenceCount.decrementAndGet();
    }

    private long getExpirationTime() {
        return _expirationTime.get();
    }

    void setExpirationTime(long expirationTime) {
        _expirationTime.set(expirationTime);
    }

    @Override
    public String toString() {
        return "TimedCacheEntry[expirationTime=" + _expirationTime.get()
                + ", value=" + toString(getValue()) + ", key="
                + getKey().toString() + "]";
    }

}
