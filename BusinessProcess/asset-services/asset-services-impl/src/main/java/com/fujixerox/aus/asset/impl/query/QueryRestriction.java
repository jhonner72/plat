package com.fujixerox.aus.asset.impl.query;

import com.fujixerox.aus.asset.api.beans.tuples.Pair;
import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.query.IQueryCondition;
import com.fujixerox.aus.asset.api.query.IQueryRestriction;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class QueryRestriction extends
        Pair<QueryRestriction, IAttribute, IQueryCondition> implements
        IQueryRestriction<QueryRestriction> {

    public QueryRestriction(IAttribute attribute, IQueryCondition condition) {
        super(attribute, condition);
    }

    @Override
    public IAttribute getAttribute() {
        return getFirst();
    }

    @Override
    public IQueryCondition getCondition() {
        return getSecond();
    }

    @Override
    public IAttributeHandler getHandler() {
        return getAttribute().getHandler();
    }

    @Override
    public String getLowValue() {
        return getCondition().getLowValue();
    }

    @Override
    public String getHighValue() {
        return getCondition().getHighValue();
    }

    @Override
    public boolean isMatchCase() {
        return getCondition().isMatchCase();
    }

}
