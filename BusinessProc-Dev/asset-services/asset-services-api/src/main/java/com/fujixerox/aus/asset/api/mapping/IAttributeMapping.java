package com.fujixerox.aus.asset.api.mapping;

import java.util.List;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IAttributeMapping {

    IAttribute getIncomingAttribute(String objectType, String attributeName);

    IAttribute getOutgoingAttributeByIncoming(IAttribute attribute);

    String getObjectType(String objectType);

    List<IAttribute> getAllIncomingAttributes(String objectType);

    String getDefaultType();

}
