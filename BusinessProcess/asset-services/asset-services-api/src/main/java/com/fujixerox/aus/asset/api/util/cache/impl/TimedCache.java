package com.fujixerox.aus.asset.api.util.cache.impl;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import com.fujixerox.aus.asset.api.util.cache.ICacheFactory;
import com.fujixerox.aus.asset.api.util.cache.ITimedCache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class TimedCache<K, V, S, E extends Throwable> implements
        ITimedCache<K, V, S, E> {

    private final ConcurrentMap<K, TimedCacheEntry<K, V>> _cache;

    private final Queue<TimedCacheEntry<K, V>> _queue;

    private final AtomicCyclicCounter _insertsCount;

    private final ICacheFactory<K, V, S, E> _cacheFactory;

    private final int _cacheInterval;

    private final boolean _withReferences;

    public TimedCache(ICacheFactory<K, V, S, E> cacheFactory, int cacheInterval) {
        this(cacheFactory, cacheInterval, Integer.MAX_VALUE, false);
    }

    public TimedCache(ICacheFactory<K, V, S, E> cacheFactory,
            int cacheInterval, long maxInsertsWORemove, boolean withReferences) {
        _cacheFactory = cacheFactory;
        _cacheInterval = cacheInterval;
        _cache = new ConcurrentHashMap<K, TimedCacheEntry<K, V>>();
        _queue = new ConcurrentLinkedQueue<TimedCacheEntry<K, V>>();
        _insertsCount = new AtomicCyclicCounter(0L, maxInsertsWORemove);
        _withReferences = withReferences;
    }

    @Override
    public V getObject(K key) throws E {
        return getObject(null, key);
    }

    public V getObject(S session, K key) throws E {
        if (key == null) {
            return null;
        }
        TimedCacheEntry<K, V> entry = doGetData(session, key);
        if (entry == null) {
            return null;
        }
        if (!_withReferences) {
            return entry.getValue();
        }
        return incrementReferenceCount(entry).getValue();
    }

    @Override
    public V putObject(K key, V object) {
        return putObject(key, object, System.currentTimeMillis()
                + _cacheInterval);
    }

    @Override
    public V putObject(K key, V object, final long expirationTIme) {
        TimedCacheEntry<K, V> data = new TimedCacheEntry<K, V>(key, object,
                expirationTIme);
        TimedCacheEntry<K, V> previous = _cache.put(key, data);
        if (previous == null) {
            _queue.add(data);
            if (_insertsCount.incrementAndGet() == 0) {
                cleanExpired();
            }
            return object;
        }
        _queue.remove(previous);
        return previous.getValue();
    }

    @Override
    public boolean updateExpirationTime(K key, int ttl) {
        TimedCacheEntry<K, V> data = getEntry(key);
        if (data == null) {
            return false;
        }
        data.setExpirationTime(System.currentTimeMillis() + ttl);
        return true;
    }

    public void cleanExpired() {
        long startTimestamp = System.currentTimeMillis();
        for (Iterator<TimedCacheEntry<K, V>> iter = _queue.iterator(); iter
                .hasNext();) {
            TimedCacheEntry<K, V> data = iter.next();
            if (data == null) {
                iter.remove();
            } else if (isExpired(data, startTimestamp)) {
                _cache.remove(data.getKey(), data);
                iter.remove();
            } else if (!data.hasReferences()) {
                break;
            }
        }
    }

    private TimedCacheEntry<K, V> doGetData(S session, K key) throws E {
        TimedCacheEntry<K, V> data = getEntry(key);
        if (data == null) {
            if (_cacheFactory == null) {
                return null;
            }
            V result = getUnCached(session, key);
            data = new TimedCacheEntry<K, V>(key, result, _cacheInterval);
            TimedCacheEntry<K, V> previous = _cache.putIfAbsent(key, data);
            if (previous == null) {
                _queue.add(data);
                if (_insertsCount.incrementAndGet() == 0) {
                    cleanExpired();
                }
                return data;
            } else {
                return previous;
            }
        } else if (data.isExpired()) {
            _cache.remove(data.getKey(), data);
            _queue.remove(data);
            return doGetData(session, key);
        } else {
            return data;
        }
    }

    public void clear() {
        for (Iterator<TimedCacheEntry<K, V>> iter = _queue.iterator(); iter
                .hasNext();) {
            TimedCacheEntry<K, V> data = iter.next();
            if (data == null) {
                iter.remove();
                continue;
            }
            _cache.remove(data.getKey(), data);
            iter.remove();
        }
    }

    private TimedCacheEntry<K, V> incrementReferenceCount(
            TimedCacheEntry<K, V> entry) {
        if (entry == null) {
            return null;
        }
        entry.incrementReferenceCount();
        return entry;
    }

    private void decrementReferenceCount(K key, V object) {
        if (!_withReferences) {
            return;
        }
        TimedCacheEntry<K, V> data = getEntry(key);
        if (data == null) {
            return;
        }
        if (data.getValue() == null) {
            if (object == null) {
                data.decrementReferenceCount();
            }
        } else if (data.getValue().equals(object)) {
            data.decrementReferenceCount();
        }
    }

    @Override
    public boolean remove(K key) {
        TimedCacheEntry<K, V> data = getEntry(key);
        if (data == null) {
            return false;
        }
        if (_withReferences && data.hasReferences()) {
            return false;
        }
        _queue.remove(data);
        _cache.remove(data.getKey(), data);
        return true;
    }

    @Override
    public V getUnCached(S session, K key) throws E {
        return _cacheFactory.getObject(session, key);
    }

    TimedCacheEntry<K, V> getEntry(K key) {
        return _cache.get(key);
    }

    boolean isExpired(TimedCacheEntry<K, V> data) {
        return isExpired(data, System.currentTimeMillis());
    }

    boolean isExpired(TimedCacheEntry<K, V> data, long timestamp) {
        return data.isExpired(timestamp);
    }

    public void release(K key, V value) {
        decrementReferenceCount(key, value);
    }

    @Override
    public String toString() {
        return _cache.toString();
    }

}
