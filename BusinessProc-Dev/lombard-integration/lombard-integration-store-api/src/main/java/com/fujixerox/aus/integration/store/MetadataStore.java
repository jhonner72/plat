package com.fujixerox.aus.integration.store;

/**
 * Created by warwick on 12/05/2015.
 */
public interface MetadataStore {
    public <T> T getMetadata(Class<T> clazz);
    public <T> int storeMetadata(T clazz);
}
