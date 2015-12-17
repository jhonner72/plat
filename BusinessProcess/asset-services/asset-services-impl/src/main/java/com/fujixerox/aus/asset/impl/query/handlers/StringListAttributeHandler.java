package com.fujixerox.aus.asset.impl.query.handlers;

import java.util.List;

import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class StringListAttributeHandler extends AbstractEnumAttributeHandler {

    private final List<String> _values;

    public StringListAttributeHandler(boolean range, List<String> values) {
        super(range);
        _values = values;
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        Interval interval = getInterval(_values, lowValue, highValue);
        if (interval == null) {
            Logger.debug("Interval is null, returning negative condition");
            return EMPTY_CONDITION;
        }
        int low = interval.getLow();
        int high = interval.getHigh();
        boolean include = interval.include();

        if (include) {
            if (low == high) {
                return equal(qualification, toQuotedString(_values.get(low)),
                        matchCase);
            }
            return in(qualification, matchCase, _values.subList(low, high + 1));
        }
        if (low == high) {
            return equal(qualification, toQuotedString(_values.get(low)),
                    matchCase);
        }
        return in(qualification, matchCase, _values.get(low), _values.get(high));
    }

}
