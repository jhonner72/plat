package com.fujixerox.aus.asset.impl.mapping;

import com.fujixerox.aus.asset.api.beans.tuples.Pair;
import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class Attribute extends Pair<Attribute, String, String> implements
        IAttribute<Attribute> {

    private final IAttributeHandler _handler;

    public Attribute(String objectType, String attributeName) {
        this(objectType, attributeName, null);
    }

    public Attribute(IAttribute attribute, IAttributeHandler handler) {
        this(attribute.getObjectType(), attribute.getName(), handler);
    }

    public Attribute(String objectType, String attributeName,
            IAttributeHandler handler) {
        super(objectType, attributeName);
        _handler = handler;
    }

    @SuppressWarnings("PMD.ShortMethodName")
    public static Attribute of(String objectType, String attributeName) {
        return new Attribute(objectType, attributeName);
    }

    @Override
    public String getObjectType() {
        return getFirst();
    }

    @Override
    public String getName() {
        return getSecond();
    }

    @Override
    public IAttributeHandler getHandler() {
        return _handler;
    }

}
