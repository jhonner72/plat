package com.fujixerox.aus.asset.ingestion.tbo;

import com.documentum.fc.client.DfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.ingestion.util.ReflectionUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class FakeSysObject extends DfSysObject {

    public FakeSysObject() {
        super();
    }

    // by default DFC calls
    // com.documentum.fc.client.DfPersistentObject.setDefaultValues
    // which has performance impact, so we override this behaviour
    @Override
    protected void init() throws DfException {
        ReflectionUtils.invokeMethod(this, "setValues");
    }

}
