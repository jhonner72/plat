package com.fujixerox.aus.asset.impl.query.handlers;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */

public class DummyAttributeHandler extends AbstractAttributeHandler {

    private final String _dummyValue;

    public DummyAttributeHandler(String dummyValue) {
        super();
        _dummyValue = dummyValue;
    }

    @Override
    protected String doGetRestriction(IDfSession session, String qualification,
            boolean matchCase, String lowValue, String highValue)
        throws DfException {
        return null;
    }

    @Override
    public String getDefaultValue() {
        return _dummyValue;
    }

}
