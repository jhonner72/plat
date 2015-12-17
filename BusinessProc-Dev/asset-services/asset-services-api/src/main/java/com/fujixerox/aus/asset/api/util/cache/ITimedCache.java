package com.fujixerox.aus.asset.api.util.cache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface ITimedCache<K, V, S, E extends Throwable> extends
        ICache<K, V, S, E> {

    void cleanExpired();

    V putObject(K key, V object, long ttl);

    V putObject(K key, V object);

    boolean updateExpirationTime(K key, int ttl);

    void release(K key, V value);

}
