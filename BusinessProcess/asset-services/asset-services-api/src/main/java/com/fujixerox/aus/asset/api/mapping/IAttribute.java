package com.fujixerox.aus.asset.api.mapping;

import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IAttribute<T> extends Comparable<T> {

    String getObjectType();

    String getName();

    IAttributeHandler getHandler();

}
