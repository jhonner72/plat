package com.fujixerox.aus.asset.impl.query;

import com.fujixerox.aus.asset.api.beans.tuples.Triplet;
import com.fujixerox.aus.asset.api.query.IQueryCondition;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class QueryCondition extends
        Triplet<QueryCondition, String, String, Boolean> implements
        IQueryCondition<QueryCondition> {

    public QueryCondition(String lowValue, String highValue) {
        this(lowValue, highValue, true);
    }

    public QueryCondition(String lowValue, String highValue, boolean matchCase) {
        super(lowValue, highValue, matchCase);
    }

    @Override
    public String getLowValue() {
        return getFirst();
    }

    @Override
    public String getHighValue() {
        return getSecond();
    }

    @Override
    public boolean isMatchCase() {
        return getThird();
    }

}
