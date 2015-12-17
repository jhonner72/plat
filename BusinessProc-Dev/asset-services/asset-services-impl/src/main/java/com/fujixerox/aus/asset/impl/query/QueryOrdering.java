package com.fujixerox.aus.asset.impl.query;

import com.fujixerox.aus.asset.api.beans.tuples.Pair;
import com.fujixerox.aus.asset.api.mapping.IAttribute;
import com.fujixerox.aus.asset.api.query.IQueryOrdering;
import com.fujixerox.aus.asset.impl.mapping.Attribute;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class QueryOrdering extends
        Pair<QueryOrdering, IAttribute, Boolean> implements
        IQueryOrdering<QueryOrdering> {

    public QueryOrdering(String objectType, String columnName,
            boolean descending) {
        this(Attribute.of(objectType, columnName), descending);
    }

    public QueryOrdering(IAttribute attribute, boolean descending) {
        super(attribute, descending);
    }

    @Override
    public IAttribute getAttribute() {
        return getFirst();
    }

    @Override
    public boolean isDescending() {
        return getSecond();
    }

}
