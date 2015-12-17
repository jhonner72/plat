package com.fujixerox.aus.asset.impl.query.handlers;

import java.util.List;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class MapStringAttributeHandler extends AbstractEnumAttributeHandler {

    private final List<String> _fromValues;

    private final List<String> _toValues;

    public MapStringAttributeHandler(boolean range, List<String> fromValues,
            List<String> toValues) {
        super(range);
        _fromValues = fromValues;
        _toValues = toValues;
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        Interval interval = getInterval(_fromValues, lowValue, highValue);
        if (interval == null) {
            Logger.debug("Interval is null, returning negative condition");
            return EMPTY_CONDITION;
        }
        int low = interval.getLow();
        int high = interval.getHigh();
        boolean include = interval.include();

        if (low > _toValues.size() - 1) {
            Logger
                    .debug(
                            "Low index ''{0}'' exceeds values {1} count, returning negative condition",
                            low, _toValues);
            return EMPTY_CONDITION;
        }

        if (high > _toValues.size() - 1) {
            Logger.debug("High index ''{0}'' exceeds values {1} count", high,
                    _toValues);
            high = _toValues.size() - 1;
        }

        if (include) {
            Logger.debug("Dealing with interval [{0}:{1}] ({2} -> {3})", low,
                    high, _fromValues, _toValues);
            if (low == high) {
                Logger
                        .debug(
                                "Low index ''{0}'' and high index ''{1}'' are equal ({2} -> {3})",
                                low, high, _fromValues, _toValues);
                return equal(qualification, toQuotedString(_toValues.get(low)),
                        matchCase);
            } else {
                Logger
                        .debug(
                                "Low index ''{0}'' and high index ''{1}'' are not equal ({2} -> {3})",
                                low, high, _fromValues, _toValues);
                return in(qualification, matchCase, _toValues.subList(low,
                        high + 1));
            }
        }
        if (low == high) {
            Logger
                    .debug(
                            "Low index ''{0}'' and high index ''{1}'' are equal ({2} -> {3})",
                            low, high, _fromValues, _toValues);
            return equal(qualification, toQuotedString(_toValues.get(low)),
                    matchCase);
        } else {
            Logger
                    .debug(
                            "Low index ''{0}'' and high index ''{1}'' are not equal ({2} -> {3})",
                            low, high, _fromValues, _toValues);
            return in(qualification, matchCase, _toValues.get(low), _toValues
                    .get(high));
        }
    }

    @Override
    public String getStringValue(IDfValue value) {
        String stringValue = value.asString();
        int index = _toValues.indexOf(stringValue);
        if (index < 0) {
            Logger.debug(
                    "Unable to find ''{0}'' in {1}, returning empty string",
                    stringValue, _toValues);
            return "";
        }
        Logger.debug("Index for ''{0}'' is ''{1}'' ({2})", stringValue, index,
                _toValues);
        if (index > _fromValues.size() - 1) {
            Logger
                    .debug(
                            "Index ''{0}'' exceeds values {1} count, returning empty string",
                            index, _fromValues);
            return "";
        }
        String result = _fromValues.get(index);
        Logger.debug("Returning ''{0}'' for ''{1}'' ({2} -> {3})", result,
                stringValue, _toValues, _fromValues);
        return result;
    }

}
