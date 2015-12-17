package com.fujixerox.aus.asset.api.util.cache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface ICacheFactory<K, V, S, E extends Throwable> {

    V getObject(S session, K key) throws E;

}
