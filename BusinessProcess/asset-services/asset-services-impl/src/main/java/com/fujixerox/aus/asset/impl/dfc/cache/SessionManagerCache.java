package com.fujixerox.aus.asset.impl.dfc.cache;

import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.api.util.cache.ITimedCache;
import com.fujixerox.aus.asset.api.util.cache.impl.TimedCache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class SessionManagerCache {

    private static final int CACHE_INTERVAL = 60 * 1000;

    private static final SessionManagerCache INSTANCE = new SessionManagerCache();

    private final ITimedCache<IDocumentumCredentials, IDfSessionManager, Void, DfException> _cache;

    private SessionManagerCache() {
        _cache = new TimedCache<IDocumentumCredentials, IDfSessionManager, Void, DfException>(
                new SessionManagerCacheFactory(), CACHE_INTERVAL,
                Long.MAX_VALUE, true);
    }

    public static IDfSessionManager getSessionManager(
            IDocumentumCredentials data) throws DfException {
        return getCache().getObject(data);
    }

    public static void releaseSessionManager(IDocumentumCredentials data,
            IDfSessionManager sessionManager) {
        getCache().updateExpirationTime(data, CACHE_INTERVAL);
        getCache().release(data, sessionManager);
    }

    private static ITimedCache<IDocumentumCredentials, IDfSessionManager, Void, DfException> getCache() {
        return INSTANCE._cache;
    }

}
