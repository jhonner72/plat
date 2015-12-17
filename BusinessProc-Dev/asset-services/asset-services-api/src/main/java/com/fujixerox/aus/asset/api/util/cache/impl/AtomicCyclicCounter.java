package com.fujixerox.aus.asset.api.util.cache.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
final class AtomicCyclicCounter {

    private final AtomicLong _atomicLong;

    private final long _maxValue;

    public AtomicCyclicCounter(long maxValue) {
        this(0, maxValue);
    }

    public AtomicCyclicCounter(long initialValue, long maxValue) {
        _atomicLong = new AtomicLong(initialValue);
        _maxValue = maxValue;
    }

    public long incrementAndGet() {
        while (true) {
            long current = _atomicLong.get();
            long next = current + 1;
            if (next > _maxValue) {
                next = 0;
            }
            if (_atomicLong.compareAndSet(current, next)) {
                return next;
            }
        }
    }

}
