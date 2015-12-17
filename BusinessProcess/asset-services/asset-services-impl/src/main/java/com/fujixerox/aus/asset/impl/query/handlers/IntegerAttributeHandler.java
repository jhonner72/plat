package com.fujixerox.aus.asset.impl.query.handlers;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IntegerAttributeHandler extends AbstractAttributeHandler {

    public IntegerAttributeHandler() {
        super();
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        if (StringUtils.isBlank(highValue)) {
            Logger.debug("High ''{0}'' is blank", highValue);
            Integer low = Integer.parseInt(lowValue);
            return greaterThanOrEqual(qualification, String.valueOf(low));
        }
        if (StringUtils.isBlank(lowValue)) {
            Logger.debug("Low ''{0}'' is blank", highValue);
            Integer high = Integer.parseInt(highValue);
            return lessThanOrEqual(qualification, String.valueOf(high));
        }
        if (isEqualBoundaries(lowValue, highValue)) {
            Logger.debug("Low ''{0}'' and high ''{1}'' are equal", lowValue,
                    highValue);
            Integer low = Integer.parseInt(lowValue);
            return equal(qualification, String.valueOf(low));
        }
        Integer low = Integer.parseInt(lowValue);
        Integer high = Integer.parseInt(highValue);
        return betweenOrEqual(qualification, String.valueOf(low), String
                .valueOf(high));
    }

}
