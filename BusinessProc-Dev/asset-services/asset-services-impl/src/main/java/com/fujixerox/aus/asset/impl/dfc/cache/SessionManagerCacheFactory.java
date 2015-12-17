package com.fujixerox.aus.asset.impl.dfc.cache;

import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.api.dfc.util.SessionUtils;
import com.fujixerox.aus.asset.api.util.cache.ICacheFactory;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SessionManagerCacheFactory
        implements
        ICacheFactory<IDocumentumCredentials, IDfSessionManager, Void, DfException> {

    public SessionManagerCacheFactory() {
        super();
    }

    @Override
    public IDfSessionManager getObject(Void session, IDocumentumCredentials key)
        throws DfException {
        IDfSessionManager sessionManager = SessionUtils.newSessionManager();
        sessionManager.setIdentity(key.getDocbaseName(), SessionUtils
                .newLoginInfo(key.getUserName(), key.getPassword(), key
                        .getDomain()));
        return sessionManager;
    }

}
