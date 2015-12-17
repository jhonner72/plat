package com.fujixerox.aus.asset.api.util.cache.impl;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
class CacheEntry<K, V> {

    private final V _value;
    private final K _key;

    public CacheEntry(CacheEntry<K, V> value) {
        this(value._key, value._value);
    }

    CacheEntry(K key, V value) {
        _value = value;
        _key = key;
    }

    public V getValue() {
        return _value;
    }

    public K getKey() {
        return _key;
    }

    @Override
    public final boolean equals(Object other) {
        return this == other;
    }

    @Override
    public final int hashCode() {
        if (_value == null) {
            return 0;
        }
        return _value.hashCode();
    }

    @Override
    public String toString() {
        return "CacheEntry[value=" + toString(_value) + ", key="
                + _key.toString() + "]";
    }

    String toString(V value) {
        if (_value == null) {
            return "null";
        }
        return _value.toString();
    }

}
