package com.fujixerox.aus.asset.api.util.cache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface ICache<K, V, S, E extends Throwable> {

    V getObject(K key) throws E;

    V getObject(S session, K key) throws E;

    V getUnCached(S session, K key) throws E;

    void clear();

    boolean remove(K key);

}
