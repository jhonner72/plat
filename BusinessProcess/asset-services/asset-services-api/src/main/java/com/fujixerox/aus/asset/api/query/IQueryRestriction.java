package com.fujixerox.aus.asset.api.query;

import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IQueryRestriction<T> extends Comparable<T> {

    IAttribute getAttribute();

    IQueryCondition getCondition();

    IAttributeHandler getHandler();

    String getLowValue();

    String getHighValue();

    boolean isMatchCase();

}
