package com.fujixerox.aus.asset.impl.query.handlers;

import org.apache.commons.lang3.StringUtils;

import com.documentum.fc.client.IDfSession;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BooleanAttributeHandler extends AbstractAttributeHandler {

    public BooleanAttributeHandler() {
        super();
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue) {
        if (StringUtils.isBlank(highValue)) {
            return greaterThanOrEqual(qualification, lowValue);
        }
        if (StringUtils.isBlank(lowValue)) {
            return lessThanOrEqual(qualification, highValue);
        }
        if (isEqualBoundaries(lowValue, highValue)) {
            return equal(qualification, lowValue);
        }
        return betweenOrEqual(qualification, lowValue, highValue);
    }

}
