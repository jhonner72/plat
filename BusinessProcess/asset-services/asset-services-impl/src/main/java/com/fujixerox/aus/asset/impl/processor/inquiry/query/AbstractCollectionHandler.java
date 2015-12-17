package com.fujixerox.aus.asset.impl.processor.inquiry.query;

import com.documentum.fc.common.IDfValue;
import com.fujixerox.aus.asset.api.dfc.query.IMetadataHandler;
import com.fujixerox.aus.asset.api.dfc.query.IRowHandler;
import com.fujixerox.aus.asset.api.query.handlers.IAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
abstract class AbstractCollectionHandler implements IRowHandler,
        IMetadataHandler {

    AbstractCollectionHandler() {
        super();
    }

    String getValue(IDfValue value, IAttributeHandler attributeHandler) {
        return attributeHandler.getStringValue(value);
    }

}
