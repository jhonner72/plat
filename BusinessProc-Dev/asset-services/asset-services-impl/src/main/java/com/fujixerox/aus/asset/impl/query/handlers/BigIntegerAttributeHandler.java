package com.fujixerox.aus.asset.impl.query.handlers;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BigIntegerAttributeHandler extends IntegerAttributeHandler {

    private static final String FLOATING_PART = ".0";

    public BigIntegerAttributeHandler() {
        super();
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        if (StringUtils.isBlank(highValue)) {
            Logger.debug("High ''{0}'' is blank", highValue);
            Long low = Long.parseLong(lowValue);
            return greaterThanOrEqual(qualification, String.valueOf(low)
                    + FLOATING_PART);
        }
        if (StringUtils.isBlank(lowValue)) {
            Logger.debug("Low ''{0}'' is blank", highValue);
            Long high = Long.parseLong(highValue);
            return lessThanOrEqual(qualification, String.valueOf(high)
                    + FLOATING_PART);
        }
        if (isEqualBoundaries(lowValue, highValue)) {
            Logger.debug("Low ''{0}'' and high ''{1}'' are equal", lowValue,
                    highValue);
            Long low = Long.parseLong(lowValue);
            return equal(qualification, String.valueOf(low) + FLOATING_PART);
        }
        Long low = Long.parseLong(lowValue);
        Long high = Long.parseLong(highValue);
        return betweenOrEqual(qualification, String.valueOf(low)
                + FLOATING_PART, String.valueOf(high) + FLOATING_PART);
    }

}
