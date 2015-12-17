package com.fujixerox.aus.asset.impl.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.mapping.IAttributeMapping;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class AttributeMapping implements IAttributeMapping {

    private final String _defaultType;

    private final Map<IAttribute, IAttribute> _mapping;

    public AttributeMapping(String defaultType,
            Map<IAttribute, IAttribute> mapping) {
        super();
        _defaultType = defaultType;
        _mapping = mapping;
    }

    public String getDefaultType() {
        return _defaultType;
    }

    @Override
    public IAttribute getIncomingAttribute(String objectType,
            String attributeName) {
        return getIncomingAttribute(Attribute.of(getObjectType(objectType),
                attributeName));
    }

    private IAttribute getIncomingAttribute(Attribute attribute) {
        for (Map.Entry<IAttribute, IAttribute> e : _mapping.entrySet()) {
            if (e.getKey().equals(attribute)) {
                return e.getKey();
            }
        }
        return null;
    }

    @Override
    public IAttribute getOutgoingAttributeByIncoming(IAttribute attribute) {
        return _mapping.get(attribute);
    }

    @Override
    public List<IAttribute> getAllIncomingAttributes(String objectType) {
        String incomingType = getObjectType(objectType);
        List<IAttribute> result = new ArrayList<IAttribute>();
        for (Map.Entry<IAttribute, IAttribute> e : _mapping.entrySet()) {
            if (e.getKey().getObjectType().equals(incomingType)) {
                result.add(e.getKey());
            }
        }
        return result;
    }

    public final String getObjectType(String objectType) {
        if (StringUtils.isBlank(objectType)) {
            return getDefaultType();
        }
        return objectType;
    }

}
