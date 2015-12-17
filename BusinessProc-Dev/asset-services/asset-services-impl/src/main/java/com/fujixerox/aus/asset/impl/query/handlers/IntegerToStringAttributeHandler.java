package com.fujixerox.aus.asset.impl.query.handlers;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.asset.api.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class IntegerToStringAttributeHandler extends AbstractAttributeHandler {

    public IntegerToStringAttributeHandler() {
        super();
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        if (StringUtils.isBlank(highValue)) {
            Logger.debug("High ''{0}'' is blank", highValue);
            Integer low = Integer.parseInt(lowValue);
            return in(qualification, low, low);
        }
        if (StringUtils.isBlank(lowValue)) {
            Logger.debug("Low ''{0}'' is blank", highValue);
            Integer high = Integer.parseInt(highValue);
            return in(qualification, high, high);
        }
        Integer low = Integer.parseInt(lowValue);
        Integer high = Integer.parseInt(highValue);
        return in(qualification, low, high);
    }

}
