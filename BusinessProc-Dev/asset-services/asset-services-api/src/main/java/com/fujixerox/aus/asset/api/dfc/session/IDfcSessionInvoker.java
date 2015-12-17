package com.fujixerox.aus.asset.api.dfc.session;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.util.IInvoker;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IDfcSessionInvoker<T> extends
        IInvoker<T, IDfSession, DfException> {

}
