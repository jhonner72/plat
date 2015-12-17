package com.fujixerox.aus.asset.api.query.handlers;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.query.IQueryRestriction;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IAttributeHandler {

    String getRestriction(IDfSession session, String qualification,
            IQueryRestriction restriction) throws DfException;

    String getRestriction(IDfSession session, String qualification, String log,
            String high) throws DfException;

    String getRestriction(IDfSession session, String qualification,
            boolean matchCase, String log, String high) throws DfException;

    String getStringValue(IDfValue value);

    String getDefaultValue();

    void dispose();

}
